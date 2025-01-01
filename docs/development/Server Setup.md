<img src="../img/icon.png" height="150" align="right"/>

# Server Setup
Password Vault contains some limited (and optional) features that make the app interact with the website [passwordvault.christian2003.de](https://passwordvault.christian2003.de). Since the server is not a part of this repository, this document contains information about the server-side setup that is required for Password Vault.

###### Table of Contents
1. [App Links](#app-links)

<br/>

## App Links
Opening a link of a specific format (e.g. `https://passwordvault.christian2003.de/data/quality_gate`) on an Android device should not open the website referred, but instead the Password Vault app. This requires a specific setup within the manifest. However, an additional server-side setup is required.

###### Assetlinks
The server needs to contain a file `assetlinks.json` available at the following URL: [https://passwordvault.christian2003.de/.well-known/assetlinks.json](https://passwordvault.christian2003.de/.well-known/assetlinks.json).

This file must contain the following code:
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "de.christian2003.passwordvault",
      "sha256_cert_fingerprints": [
        "07:FA:28:97:BF:25:3A:8E:ED:56:C1:F4:4A:3B:E6:FE:19:18:39:D5:26:9D:41:D1:75:2F:A1:B8:09:DB:15:76"
      ]
    }
  }
]
```

<br/>

***
2024-12-04  
&copy; Christian-2003
