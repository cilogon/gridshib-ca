/* Copyright (C) 1995-1998 Eric Young (eay@cryptsoft.com)
 * All rights reserved.
 *
 * This package is an SSL implementation written
 * by Eric Young (eay@cryptsoft.com).
 * The implementation was written so as to conform with Netscapes SSL.
 * 
 * This library is free for commercial and non-commercial use as long as
 * the following conditions are aheared to.  The following conditions
 * apply to all code found in this distribution, be it the RC4, RSA,
 * lhash, DES, etc., code; not just the SSL code.  The SSL documentation
 * included with this distribution is covered by the same copyright terms
 * except that the holder is Tim Hudson (tjh@cryptsoft.com).
 * 
 * Copyright remains Eric Young's, and as such any Copyright notices in
 * the code are not to be removed.
 * If this package is used in a product, Eric Young should be given attribution
 * as the author of the parts of the library used.
 * This can be in the form of a textual message at program startup or
 * in documentation (online or textual) provided with the package.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes cryptographic software written by
 *     Eric Young (eay@cryptsoft.com)"
 *    The word 'cryptographic' can be left out if the rouines from the library
 *    being used are not cryptographic related :-).
 * 4. If you include any Windows specific code (or a derivative thereof) from 
 *    the apps directory (application code) you must include an acknowledgement:
 *    "This product includes software written by Tim Hudson (tjh@cryptsoft.com)"
 * 
 * THIS SOFTWARE IS PROVIDED BY ERIC YOUNG ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * The licence and distribution terms for any publically available version or
 * derivative of this code cannot be changed.  i.e. this code cannot simply be
 * copied and put under another distribution licence
 * [including the GNU Public Licence.]
 */

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <openssl/bio.h>
#include <openssl/asn1.h>
#include <openssl/err.h>
#include <openssl/bn.h>
#include <openssl/evp.h>
#include <openssl/x509.h>
#include <openssl/x509v3.h>
#include <openssl/objects.h>
#include <openssl/pem.h>

#include "apps.h"

#undef POSTFIX
#define	POSTFIX	".srl"
#define DEF_LIFETIME	8 * 60 * 60 /* 8 hours */

#define SAML_EXT_OID "1.3.6.1.4.1.3536.1.1.1.10"

static const char *x509_usage[]={
"usage: x509 args\n",
" -in arg         - input file - default stdin\n",
" -out arg        - output file - default stdout\n",
" -lifetime arg   - Lifetime of signed certificate in seconds- def 8 hours\n",
" -subj arg       - set the subject DN in the signed certificate.\n",
" -utf8           - input characters are UTF8 (default ASCII)\n",
" -multivalue-rdn - enable support for multivalued RDNs\n",
" -CA arg         - set the CA certificate, must be PEM format.\n",
" -CAkey arg      - set the CA key, must be PEM format\n",
"                   missing, it is assumed to be in the CA file.\n",
" -CAcreateserial - create serial number file if it does not exist\n",
" -CAserial arg   - serial file\n",
" -set_serial     - serial number to use\n",
" -md2/-md5/-sha1/-mdc2 - digest to use\n",
" -samlExt        - file with SAML extension to add\n",
" -extfile        - configuration file with X509V3 extensions to add\n",
" -clrext         - delete extensions before signing and input certificate\n",
NULL
};

static int readFile(char *, unsigned char **, int *);
static X509_EXTENSION *create_SAML_extension(char *);
static int x509_certify (X509_STORE *ctx,char *CAfile,const EVP_MD *digest,
			 X509 *x,X509 *xca,EVP_PKEY *pkey,char *serial,
			 int create,long lifetime, int clrext, CONF *conf, char *section,
                         ASN1_INTEGER *sno);
EVP_PKEY *load_key(BIO *err, const char *file, int format, int maybe_stdin,
                   const char *pass, ENGINE *e, const char *key_descrip);

static int reqfile=1;

int main(int, char **);

int main(int argc, char **argv)
{
    ENGINE *e = NULL;
    int ret=1;
    X509_REQ *req=NULL;
    X509 *x=NULL,*xca=NULL;
    EVP_PKEY *CApkey=NULL;
    ASN1_INTEGER *sno = NULL;
    int i,num,badops=0;
    BIO *out=NULL;
    BIO *STDout=NULL;
    int informat = FORMAT_PEM;
    int outformat = FORMAT_PEM;
    int keyformat = FORMAT_PEM;
    int CAformat = FORMAT_PEM;
    int CAkeyformat = FORMAT_PEM;
    char *infile=NULL,*outfile=NULL,*keyfile=NULL,*CAfile=NULL;
    char *CAkeyfile=NULL,*CAserial=NULL;
    char *subj=NULL;
    int multirdn = 0;
    unsigned long chtype = MBSTRING_ASC;
    int CA_flag=0,CA_createserial=0;
    int clrext=0;
    int x509req=0;
    long lifetime=DEF_LIFETIME;
    const char **pp;
    X509_STORE *ctx=NULL;
    const EVP_MD *md_alg,*digest=EVP_sha1();
    CONF *extconf = NULL;
    char *extsect = NULL, *extfile = NULL;
    char *samlExtFile = NULL;
    X509_EXTENSION *samlExt = NULL;
    int need_rand = 1;
    char *engine=NULL;
    EVP_PKEY *pkey;
    X509_CINF *ci;
    BIO *in;


    bio_err=BIO_new_fp(stderr,BIO_NOCLOSE);

    STDout=BIO_new_fp(stdout,BIO_NOCLOSE);

    ctx=X509_STORE_new();
    if (ctx == NULL) goto end;
    if (!X509_STORE_set_default_paths(ctx))
    {
        ERR_print_errors(bio_err);
        goto end;
    }

    argc--;
    argv++;
    num=0;
    while (argc >= 1)
    {
        if (strcmp(*argv,"-lifetime") == 0)
        {
            if (--argc < 1) goto bad;
            lifetime=atol(*(++argv));
            if (lifetime == 0)
            {
                BIO_printf(STDout,"bad lifetime\n");
                goto bad;
            }
        }
        else if (strcmp(*argv,"-extfile") == 0)
        {
            if (--argc < 1) goto bad;
            extfile= *(++argv);
        }
        else if (strcmp(*argv,"-extensions") == 0)
        {
            if (--argc < 1) goto bad;
            extsect= *(++argv);
        }
        else if (strcmp(*argv,"-in") == 0)
        {
            if (--argc < 1) goto bad;
            infile= *(++argv);
        }
        else if (strcmp(*argv,"-out") == 0)
        {
            if (--argc < 1) goto bad;
            outfile= *(++argv);
        }
        else if (strcmp(*argv,"-CA") == 0)
        {
            if (--argc < 1) goto bad;
            CAfile= *(++argv);
            CA_flag= ++num;
            need_rand = 1;
        }
        else if (strcmp(*argv,"-CAkey") == 0)
        {
            if (--argc < 1) goto bad;
            CAkeyfile= *(++argv);
        }
        else if (strcmp(*argv,"-CAserial") == 0)
        {
            if (--argc < 1) goto bad;
            CAserial= *(++argv);
        }
        else if (strcmp(*argv,"-set_serial") == 0)
        {
            if (--argc < 1) goto bad;
            if (!(sno = s2i_ASN1_INTEGER(NULL, *(++argv))))
goto bad;
        }
        else if (strcmp(*argv,"-CAcreateserial") == 0)
CA_createserial= ++num;
        else if (strcmp(*argv,"-clrext") == 0)
clrext = 1;
        else if (strcmp(*argv,"-subj") == 0)
        {
            if (--argc < 1) goto bad;
            subj = *(++argv);
        }
        else if (strcmp(*argv,"-multivalue-rdn") == 0)
multirdn=1;
        else if (strcmp(*argv,"-utf8") == 0)
chtype = MBSTRING_UTF8;
        else if (strcmp(*argv,"-samlExt") == 0)
        {
            if (--argc < 1) goto bad;
            samlExtFile = *(++argv);
        }
        else if ((md_alg=EVP_get_digestbyname(*argv + 1)))
        {
            /* ok */
            digest=md_alg;
        }
        else
        {
            BIO_printf(bio_err,"unknown option %s\n",*argv);
            badops=1;
            break;
        }
        argc--;
        argv++;
    }

    if (badops)
    {
      bad:
        for (pp=x509_usage; (*pp != NULL); pp++)
BIO_printf(bio_err,"%s",*pp);
        goto end;
    }

    app_RAND_load_file(NULL, bio_err, 0);

    ERR_load_crypto_strings();

    if ((CAkeyfile == NULL) && (CA_flag) && (CAformat == FORMAT_PEM))
    { CAkeyfile=CAfile; }
    else if ((CA_flag) && (CAkeyfile == NULL))
    {
        BIO_printf(bio_err,"need to specify a CAkey if using the CA command\n");
        goto end;
    }

    if (extfile)
    {
        long errorline = -1;
        X509V3_CTX ctx2;
        extconf = NCONF_new(NULL);
        if (!NCONF_load(extconf, extfile,&errorline))
        {
            if (errorline <= 0)
BIO_printf(bio_err,
           "error loading the config file '%s'\n",
           extfile);
            else
BIO_printf(bio_err,
           "error on line %ld of config file '%s'\n"
           ,errorline,extfile);
            goto end;
        }
        if (!extsect)
        {
            extsect = NCONF_get_string(extconf, "default", "extensions");
            if (!extsect)
            {
                ERR_clear_error();
                extsect = "default";
            }
        }
        X509V3_set_ctx_test(&ctx2);
        X509V3_set_nconf(&ctx2, extconf);
        if (!X509V3_EXT_add_nconf(extconf, &ctx2, extsect, NULL))
        {
            BIO_printf(bio_err,
                       "Error Loading extension section %s\n",
                       extsect);
            ERR_print_errors(bio_err);
            goto end;
        }
    }       

    if (samlExtFile)
    {
        samlExt = create_SAML_extension(samlExtFile);
        if (samlExt == NULL)
        {
            /* Error already printed in create_SAML_extension() */
            goto end;
        }
    }
    
    if (!CA_flag)
    {
        BIO_printf(bio_err,"We need a private key to sign with\n");
        goto end;
    }
    in=BIO_new(BIO_s_file());
    if (in == NULL)
    {
        ERR_print_errors(bio_err);
        goto end;
    }

    if (infile == NULL)
BIO_set_fp(in,stdin,BIO_NOCLOSE|BIO_FP_TEXT);
    else
    {
        if (BIO_read_filename(in,infile) <= 0)
        {
            perror(infile);
            BIO_free(in);
            goto end;
        }
    }
    req=PEM_read_bio_X509_REQ(in,NULL,NULL,NULL);
    BIO_free(in);

    if (req == NULL)
    {
        ERR_print_errors(bio_err);
        goto end;
    }

    if (	(req->req_info == NULL) ||
                (req->req_info->pubkey == NULL) ||
                (req->req_info->pubkey->public_key == NULL) ||
                (req->req_info->pubkey->public_key->data == NULL))
    {
        BIO_printf(bio_err,"The certificate request appears to corrupted\n");
        BIO_printf(bio_err,"It does not contain a public key\n");
        goto end;
    }
    if ((pkey=X509_REQ_get_pubkey(req)) == NULL)
    {
        BIO_printf(bio_err,"error unpacking public key\n");
        goto end;
    }

    if ((x=X509_new()) == NULL) goto end;
    ci=x->cert_info;

    if (sno == NULL)
    {
        sno = ASN1_INTEGER_new();
        if (!sno || !rand_serial(NULL, sno))
goto end;
        if (!X509_set_serialNumber(x, sno)) 
goto end;
        ASN1_INTEGER_free(sno);
        sno = NULL;
    }
    else if (!X509_set_serialNumber(x, sno)) 
goto end;

    if (!X509_set_issuer_name(x,req->req_info->subject)) goto end;
    if (subj == NULL)
    {
        if (!X509_set_subject_name(x,req->req_info->subject)) goto end;
    }
    else
    {
        /* Use provided DN in certificateDN */
        X509_NAME *subject = NULL;
                    
        subject = parse_name(subj, chtype, multirdn);

        if (subject == NULL)
        {
            /* Error already printed by print_name() */
            goto end;
        }
                    
        if (!X509_set_subject_name(x,subject))
        {
            BIO_printf(bio_err,"Could not set certificate subject\n");
            ERR_print_errors(bio_err);
            X509_NAME_free(subject);
            goto end;
        }

        X509_NAME_free(subject);
    }
                
    X509_gmtime_adj(X509_get_notBefore(x),0);
    X509_gmtime_adj(X509_get_notAfter(x),lifetime);

    pkey = X509_REQ_get_pubkey(req);
    X509_set_pubkey(x,pkey);
    EVP_PKEY_free(pkey);

    if (samlExt != NULL)
    {
        if (!X509_add_ext(x, samlExt, -1 /* at end */))
        {
            BIO_printf(bio_err, "Failed to add SAML extension to certifiate\n");
            goto end;
        }
        X509_EXTENSION_free(samlExt);
        samlExt = NULL;
    }
    
    if (x == NULL) goto end;
    if (CA_flag)
    {
        xca=load_cert(bio_err,CAfile,CAformat,NULL,e,"CA Certificate");
        if (xca == NULL) goto end;
    }

    BIO_printf(bio_err,"Getting CA Private Key\n");
    if (CAkeyfile != NULL)
    {
        CApkey=load_key(bio_err,
                        CAkeyfile, CAkeyformat,
                        0, NULL, e,
                        "CA Private Key");
        if (CApkey == NULL) goto end;
    }
#ifndef OPENSSL_NO_DSA
    if (CApkey->type == EVP_PKEY_DSA)
    {
        digest=EVP_dss1();
    }
#endif
				
    assert(need_rand);
    if (!x509_certify(ctx,CAfile,digest,x,xca,
                      CApkey, CAserial,CA_createserial,lifetime, clrext,
                      extconf, extsect, sno))
    {
        goto end;
    }       
    
    out=BIO_new(BIO_s_file());
    if (out == NULL)
    {
        ERR_print_errors(bio_err);
        goto end;
    }
    if (outfile == NULL)
    {
        BIO_set_fp(out,stdout,BIO_NOCLOSE);
    }
    else
    {
        if (BIO_write_filename(out,outfile) <= 0)
        {
            perror(outfile);
            goto end;
        }
    }

    i=PEM_write_bio_X509(out,x);

    if (!i)
    {
        BIO_printf(bio_err,"unable to write certificate\n");
        ERR_print_errors(bio_err);
        goto end;
    }
    ret=0;
  end:
    if (need_rand)
    {
        app_RAND_write_file(NULL, bio_err);
    }
    OBJ_cleanup();
    NCONF_free(extconf);
    BIO_free_all(out);
    BIO_free_all(STDout);
    X509_REQ_free(req);
    X509_free(x);
    X509_free(xca);
    EVP_PKEY_free(CApkey);
    ASN1_INTEGER_free(sno);
    OPENSSL_EXIT(ret);
}

static ASN1_INTEGER *x509_load_serial(char *CAfile, char *serialfile, int create)
{
    char *buf = NULL, *p;
    ASN1_INTEGER *bs = NULL;
    BIGNUM *serial = NULL;
    size_t len;

    len = ((serialfile == NULL)
           ?(strlen(CAfile)+strlen(POSTFIX)+1)
           :(strlen(serialfile)))+1;
    buf=OPENSSL_malloc(len);
    if (buf == NULL) { BIO_printf(bio_err,"out of mem\n"); goto end; }
    if (serialfile == NULL)
    {
        BUF_strlcpy(buf,CAfile,len);
        for (p=buf; *p; p++)
        {
            if (*p == '.')
            {       
                *p='\0';
                break;
            }
        }
        BUF_strlcat(buf,POSTFIX,len);
    }
    else
    {
        BUF_strlcpy(buf,serialfile,len);
    }

    serial = load_serial(buf, create, NULL);
    if (serial == NULL) goto end;

    if (!BN_add_word(serial,1))
    {   
        BIO_printf(bio_err,"add_word failure\n"); goto end;
    }

    if (!save_serial(buf, NULL, serial, &bs)) goto end;

  end:
    if (buf) OPENSSL_free(buf);
    BN_free(serial);
    return bs;
}

static int x509_certify(X509_STORE *ctx, char *CAfile, const EVP_MD *digest,
                        X509 *x, X509 *xca, EVP_PKEY *pkey, char *serialfile, int create,
                        long lifetime, int clrext, CONF *conf, char *section, ASN1_INTEGER *sno)
{
    int ret=0;
    ASN1_INTEGER *bs=NULL;
    X509_STORE_CTX xsc;
    EVP_PKEY *upkey;

    upkey = X509_get_pubkey(xca);
    EVP_PKEY_copy_parameters(upkey,pkey);
    EVP_PKEY_free(upkey);

    if(!X509_STORE_CTX_init(&xsc,ctx,x,NULL))
    {
        BIO_printf(bio_err,"Error initialising X509 store\n");
        goto end;
    }
    if (sno) bs = sno;
    else if (!(bs = x509_load_serial(CAfile, serialfile, create)))
goto end;

    /*	if (!X509_STORE_add_cert(ctx,x)) goto end;*/

    /* NOTE: this certificate can/should be self signed, unless it was
     * a certificate request in which case it is not. */
    X509_STORE_CTX_set_cert(&xsc,x);
    if (!reqfile && !X509_verify_cert(&xsc))
    {
        goto end;
    }
    

    if (!X509_check_private_key(xca,pkey))
    {
        BIO_printf(bio_err,"CA certificate and CA private key do not match\n");
        goto end;
    }

    if (!X509_set_issuer_name(x,X509_get_subject_name(xca))) goto end;
    if (!X509_set_serialNumber(x,bs)) goto end;

    if (X509_gmtime_adj(X509_get_notBefore(x),0L) == NULL)
goto end;

    /* hardwired expired */
    if (X509_gmtime_adj(X509_get_notAfter(x),lifetime) == NULL)
goto end;

    if (clrext)
    {
        while (X509_get_ext_count(x) > 0) X509_delete_ext(x, 0);
    }

    if (conf)
    {
        X509V3_CTX ctx2;
        X509_set_version(x,2); /* version 3 certificate */
        X509V3_set_ctx(&ctx2, xca, x, NULL, NULL, 0);
        X509V3_set_nconf(&ctx2, conf);
        if (!X509V3_EXT_add_nconf(conf, &ctx2, section, x)) goto end;
    }

    if (!X509_sign(x,pkey,digest)) goto end;
    ret=1;
  end:
    X509_STORE_CTX_cleanup(&xsc);
    if (!ret)
ERR_print_errors(bio_err);
    if (!sno) ASN1_INTEGER_free(bs);
    return ret;
}

/*
 * Read data from filename. Return data in allocated buffer *pbuf with length
 * in *pbuf_len. Returns -1 on error, 0 otherwise.
 */
static int
readFile(char           *filename,
         unsigned char  **pbuf,
         int            *pbuf_len)
{
    FILE                    *file = NULL;
    unsigned char           *buf = NULL;
    /* Size of buffer */
    int                     buf_size = 0;
    /* Length of data read */
    int                     data_len = 0;
    int                     read;

    *pbuf = NULL;
    *pbuf_len = 0;
        
    file = fopen(filename, "r");
    if (!file)
    {
        BIO_printf(bio_err, "Cannot open file '%s'\n",
                   filename);
        return(-1);
    }
        
    do
    {
        if (buf_size == data_len)
        {
            buf_size += 512;
            /* First time through this is essentially a malloc() */
            buf = realloc(buf, buf_size);
            if (buf == NULL)
            {
                BIO_printf(bio_err, "Memory allocation failed (size = %d)\n",
                           buf_size);
                return(-1);
                }       
            }   
                        
            read = fread(&buf[data_len], 1, buf_size - data_len, file);
            data_len += read;
        }
        while (read > 0);
        
        if (ferror(file))
        {
            BIO_printf(bio_err, "Error reading from file '%s'\n",
                       filename);
            if (buf)
            {
                free(buf);
                return(-1);
            }
        }
        fclose(file);

        *pbuf = buf;
        *pbuf_len = data_len;
        
        return(0);
}

X509_EXTENSION *
create_SAML_extension(char *samlExtFilename)
{
    unsigned char               *ext_buf = NULL;
    int                         ext_buf_len = 0;
    X509_EXTENSION*             ext = NULL;
    ASN1_OBJECT *               oid_obj = NULL;
    ASN1_OCTET_STRING *         ext_DER_string = NULL;


    if (readFile(samlExtFilename, &ext_buf, &ext_buf_len))
    {
        BIO_printf(bio_err, "Failed to read SAML extension file '%s'\n",
                   samlExtFilename);
        goto end;
    }
    
    oid_obj = OBJ_txt2obj(SAML_EXT_OID,
                          0 /* Allow short and long names */);
        
    if (oid_obj == NULL)
    {
        BIO_printf(bio_err, "Error parsing SAML OID '%s'\n",
                   SAML_EXT_OID);
        goto end;
    }
        
    ext_DER_string = ASN1_OCTET_STRING_new();
    if (ext_DER_string == NULL)
    {
        BIO_printf(bio_err, "Could not create new ASN.1 string for SAML extension\n");
        goto end;
    }
    
    if (!ASN1_OCTET_STRING_set(ext_DER_string, ext_buf, ext_buf_len))
    {
        BIO_printf(bio_err, "Could not fill ASN.1 string for SAML extension\n");
        goto end;
    }
    
    ext = X509_EXTENSION_create_by_OBJ(
        NULL,
        oid_obj,
        0 /* not critical */,
        ext_DER_string);
    
    if (ext == NULL)
    {
        BIO_printf(bio_err, "Failed to create SAML Extension\n");
        goto end;
    }
    
  end:
    if (ext_buf != NULL)
    {
        free(ext_buf);
    }
    if (oid_obj)
    {  
        ASN1_OBJECT_free(oid_obj);
    }  

    return ext; /* May be NULL on error */
}

