## Getting Started

These instructions will get you an example of how to use AWS KMS key for encrypting and decrypting any confidential text.
I have provided Utilites class which has all the methods for encrypting and decrypting given text.
I have also provided unit test for self verification.

Basically this code does below things:
1) generates data key from AWS KMS Master key
2) encrypt and encode this data key so that it can be stored outside safely
3) distribute this key in string format (serialize) to both encryption party and decryption party
4) encryption party will decrypt and decode same data key (Using AWS KMS Master key) to encypt confidential text
5) decryption party will also decrypt and decode same data key (Using AWS KMS Master key) to decrypt confidential
text, encrypted in step #4.

You can also use this code to safely export and import aws data key as its encrypted and encoded.


### Prerequisites

1) You must have configured AWS Credentials on your machine. Use below link for how to do that:
http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html

2) Your AWS user (configure in above step) must have rights to access AWS KMS Master key in order to generate datakey,
encrypt data and decrypt the data key.

### Installing

1) Clone this repository local on your machine.

2) Follow Prerequisites as above.

3) Use maven to build and run the unit tests.

mvn clean install

## Authors

* **Himanshu Parmar** - *Initial work* - [Himanshu Parmar](https://github.com/himanshu-parmar-bigdata)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

