<img src="docs/img/icon.png" height="150" align="right">

# Security Policy
This policy defines the security measures, principles and practices that govern the design, architecture and implementations of the Password Vault password manager for Android. It's goal is to protect user data against unauthorized access, tampering or loss.

###### Table of Contents
1. [Scope](#scope)
2. [Data and Security](#data-and-security)
3. [Development Practices](#development-practices)
4. [Incident Response](#incident-response)
5. [User Responsibilities](#user-responsibilities)

<br/>

## Scope
This policy applies to:

* The Password Vault Android application
* All user data stored by the application
* All processes and development practices related to the application

<br/>

## Data and Security

###### Security Principles
* **Local-Only Storage:** All user data is stored exclusively on the user's device. No account data is transmitted to servers operated by us.
* **Zero-Knowledge:** We have no access to user passwords, vault contents or encryption keys.
* **Defense in Depth:** Multiple layers of protection are appliedm including strong encryption, key management and platform security features.
* **Privacy by Design:** Only the minimal necessary metadata is stored. Sensitive data is nevver logged or exposed.

###### Data Protection
* **Encryption Algorithm:** All sensitive data is encrypted using AES-256 in Galois/Counter Mode (GCM) for confidentiality and integrity.
* **Key Management:**
    * A master key is generated and stored securely in the Android Keystore, marked as non-exportable.
    * Each account record is associated with a unique type 4 UUID. A key is deterministically derived per record using HKDF / HMAC with the master key and UUID.
    * Derived keys are never stored. They are generated on demand.
* **Initialization Vectors (IVs):** Fresh cryptographically strong random IVs are generated for each encryption operation.
* **Integrity Protection:** AES-GCM authentication tags ensure tamper detection.

###### Authentication and Access Control
* **Vault Access:** Protected by device-level authentication (PIN, password or biometrics).
* **Session Locking:** The vault auto-locks when the app is in background or the device is locked.
* **Clipboad Handling:** Data copied to the clipboard is cleared after a short time to reduce exposure.
* **Screenshots:** Sensitive screens are protected by Android's `FLAG_SECURE` to prevent capture.

###### Backup and Recovery
* By default, no user data is backed up to the cloud or any external servers.
* Users may optionally create an encrypted backup package, protected by a user-chosen passphrase.
* If the device is lost or reset without a backup, vault data is unrecoverable.

###### Threat Mitigation
* **Malware / Rooted Devices:** Users are warned that rooted or compromised devices reduce protection guarantees.
* **Downgrade / Replay Attacks:** All encrypted data includes versioning and integrity metadata to prevent rollbacks.
* **Logging:** Secrets are never written to logs or analytics.

<br/>

## Development Practices

###### Development and Testing
* Cryptographic implementations use Android's official APIs (`Keystore`, `Cipher`).
* Code is regularly reviewed for adherence to security coding practices.
* Automated tests verify encryption correctness and tamper detection.
* Dependencies are monitored and kept up to date.

###### Encryption Usages
The following table shows which versions use this encryption mode for different purposes:

Code | Version | Data | Backup | User Password | Autofill Cache | Password Recovery
--- | --- | --- | --- | --- | --- | ---
29 | 3.7.2 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
26 | 3.7.1 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
24 | 3.7.0 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
22 | 3.6.2 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
21 | 3.6.1 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
20<sup>2</sup> | 3.6.0<sup>2</sup> | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: | :white_check_mark:
19 | 3.5.6 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
18 | 3.5.5 | :white_check_mark: | :white_check_mark:<sup>1</sup> | :white_check_mark: | :white_check_mark: |
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
_<sup>2</sup> Version 3.6.0 is only available on the Gooogle Play Store for internal testing._

<br/>

## Incident Response
If a vulnerability is discovered, users will be informed promptly. Security patches will be delivered through app updates on GitHub. The app will notify users once new versions are available for download.

Users are encouraged to report vulnerabilities via [passwordvault@christian2003.de](mailto:passwordvault@christian2003.de).

<br/>

## User Responsibilities
In order to ensure the security of user data, the app relies on the user. User responsibilities include, but are not limited to, the following:

* Maintain a secure device lock (PIN, password or biometrics).
* Do not root or jailbreak the device.
* Create backups if recovery is desired.

<br/>

***
2025-08-29  
&copy; Christian-2003  
