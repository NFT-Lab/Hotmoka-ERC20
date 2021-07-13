package io.nfteam.nftlab.hotmoka.erc20.presets;

import io.nfteam.nftlab.hotmoka.erc20.extensions.ERC20Burnable;
import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.math.UnsignedBigInteger;

public class ERC20PresetFixedSupply extends ERC20Burnable {

  /**
   * Mints a fixed amount of Tokens when constructed
   * @param name_ name of the token
   * @param symbol_ token's symbol
   * @param initialSupply Initial fixed supply
   * @param owner owner of the contract to mint initial supply to
   */
  @FromContract
  public ERC20PresetFixedSupply(String name_, String symbol_, UnsignedBigInteger initialSupply, Contract owner) {
    super(name_, symbol_);
    this._mint(owner, initialSupply);
  }

  /**
   * Mints a fixed amount of Tokens when constructed
   * @param name_ name of the token
   * @param symbol_ token's symbol
   * @param initialSupply Initial fixed supply
   * @param owner owner of the contract to mint initial supply to
   * @param generateEvents Generate events or not
   */
  @FromContract
  public ERC20PresetFixedSupply(String name_, String symbol_, UnsignedBigInteger initialSupply, Contract owner, boolean generateEvents) {
    super(name_, symbol_, generateEvents);
    this._mint(owner, initialSupply);
  }
}
