# Field level encryption

This demo showcases a method to enhance the security of users sensitive data. Our goal is to ensure that only the users themselves can access their data and prevent any unauthorized access by other parties. Additionally, we enable users to access their data simultaneously from multiple devices. All of this is achieved by leveraging the powerful capabilities of MongoDB services.

## Considerations

The purpose of this demo is to show how can we leverage the MongoDB and  resources to provide end-to-end field level encryption to users with multi-device access. 

## Overview

The encryption would be done using the Android keystore system. It offers enhanced security in key handling, any cryptographic key stored in this container is protected from unathorized use. Once a key is in the Android Keystore it can be used for different cryptographic operations, but it is not exportable.

If we only relied on the Android KeyStore to handle the encryption keys, the user would only had access to the data from a single device, keys won't be exportable into other devices. To overcome this issue we have introduced a user bound keystore in Atlas, it stores the user's keys, and it allows to import them into any new device.


## User keystore

This keystore is stored in atlas and it is accessible to the user via `CustomData`. Although the custom data is only accessible to the user, any admin with db access would be able to access to the user keys and thus to the encrypted fields. We need to also encrypt the keystore contents.

To facilitate device roaming the keystore encryption key is password generated. This way a user would be able to generate the key on any new device if they facilitate the right passphrase.

## Importing and creating keys

The user keystore is not intended to serve as the primary keystore since the Android key store provides a higher level of security.

The system keystore serves as a repository for importing keys, which are then utilized for cryptographic operations. If a user needs to generate a new key, it will be stored not only in the system keystore but also in the user's personal keystore. This ensures that the key is accessible across different devices later on.

## Accessing data

Once the keys are present in the system and combined with the encryption algorithm specification defined in the user's custom data, we can secure the user's data.

In this sample we have created the `SecureStringDelegate` a helper that provides seamless access to the secured data, as if it was a regular property. 

There are some known issues around data modelling right now. First it would convenient if we were able to access the user custom data from an object, that would facilitate accessing the cipher algorithm and key. Second, when we add support for custom type adapters we would be able to collapse the secured and accessor in a single property.

## Vector attacks

The algorithm used to secure the user keystore in Atlas is prone to brute force attacks, anybody with with access could attempt an attack. 

Another weak point is that during the import phase the keys are available unencrypted in users unsecured memory region.

There are other alternatives to using a password based key, for example, the keys could be provided by an external repository or we could even implement a decentralized process where devices could exchange keystores securely using asymetric keys. This process would be a more complex, and would require at least one device online to grant access to the data.
