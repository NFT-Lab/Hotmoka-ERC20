# Hotmoka ERC20

[![Build](https://github.com/NFT-Lab/Hotmoka-ERC20/actions/workflows/build.yml/badge.svg)](https://github.com/NFT-Lab/Hotmoka-ERC20/actions/workflows/build.yml)

## Usage
To add ERC20 contracts and use them in your project first add the dependency in your `pom.xml`:
```xml
<dependency>
  <groupId>io.nfteam.nftlab</groupId>
  <artifactId>hotmoka-erc20</artifactId>
  <version>0.1</version>
</dependency>
```
Then create a simple contract, extending ERC20 directly or an extension to have addtional functionalities:
```java
public class MyERC20 extends ERC20 {
  public MyERC20() {
      // Token name and symbol
      super("TokenName", "TKS");
  }
}
```
If instead you just want a simple ERC20 implementation on the go, the ERC20PresetFixedSupply implementation provides so,
just compile it and deploy it to have a simple ERC20 running on the Hotmoka blockchain.

## Contributing
[Feature flow workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow) is adopted
### Standards
The CI tests over the [spotless](https://github.com/diffplug/spotless) code formatting, before submitting pull requests do not
forget to run
```shell
mvn spotless:apply
```
to apply the code formatting
