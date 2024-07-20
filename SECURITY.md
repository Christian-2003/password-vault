<img src="docs/img/icon.png" height="150" align="right">

# Security Policy
This document contains information regarding the security of Password Vault.

###### Table of Contents
1. [Data Encryption](#data-encryption)
2. [Security Updates](#security-updates)
3. [Reporting a Vulnerability](#reporting-a-vulnerability)

<br/>

## Data Encryption
Password Vault encrypts your data using **AES / GCM** to ensure maximum security. The following table shows which versions use this encryption mode for different purposes:

Code | Version | Data | Backup | User Password | Autofill Cache | Password Recovery
--- | --- | --- | --- | --- | --- | ---
20 | 3.6.0 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
19 | 3.5.6 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.5.5 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.5.4 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.5.3 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.5.2 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.5.1 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.5.0 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
1 | 3.4.0 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | |
1 | 3.3.0 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | |
1 | 3.2.1 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | |
1 | 3.2.0 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | |
1 | 3.1.0 | :white_check_mark: | :x: | | |
1 | 3.0.0 | :white_check_mark: | | | |
1 | 2.2.1 | :white_check_mark: | | | |
1 | 2.2.0 | :x: | | | |
1 | 2.1.0 | :x: | | | |
1 | 2.0.0 | :x: | | | |
1 | 1.0.1 | :x: | | | |
1 | 1.0.0 | :x: | | | |

_<sup>1</sup> Backups can be encrypted, but they do not have to be encrypted._ 

<br/>

## Security Updates
Security vulnerabilities are patched as soon as they are noticed. However, such patches are only provided to the newest version. Users are not notified about security updates.

###### Recommendations for Updating to Password Vault 3
It is highly recommended for users of **Password Vault 1** and **Password Vault 2** to immediately update to **Password Vault 3**. This version is intended to be supported long term.

<br/>

## Reporting a Vulnerability
If you find any security-related bugs in our product, please notify our development team through [passwordvault@christian2003.de](mailto:passwordvault@christian2003.de) and provide as many details as possible, such as steps for reproduction.

<br/>

***
2024-07-20 
&copy; Christian-2003  
