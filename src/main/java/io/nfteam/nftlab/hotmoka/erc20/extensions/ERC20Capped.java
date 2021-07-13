package io.nfteam.nftlab.hotmoka.erc20.extensions;

import io.nfteam.nftlab.hotmoka.erc20.ERC20;
import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.lang.Takamaka;
import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;

public abstract class ERC20Capped extends ERC20 {

  private final UnsignedBigInteger _cap;

  /**
   * Sets the value of the `cap`. This value is immutable, it can only be
   * set once during construction.
   */
  public ERC20Capped(String name_, String symbol_, UnsignedBigInteger cap) {
    super(name_, symbol_);
    this._cap = cap;
  }

  /**
   * Sets the value of the `cap`. This value is immutable, it can only be
   * set once during construction.
   */
  public ERC20Capped(String name_, String symbol_, UnsignedBigInteger cap, boolean generateEvents) {
    super(name_, symbol_, generateEvents);
    this._cap = cap;
  }

  /**
   * Sets the value of the `cap`. This value is immutable, it can only be
   * set once during construction.
   */
  @View
  public UnsignedBigInteger cap() {
    return _cap;
  }

  /**
   * @see ERC20#_mint
   */
  @FromContract
  @Override
  protected void _mint(Contract account, UnsignedBigInteger amount) {
    Takamaka.require(super.totalSupply().add(amount).compareTo(cap()) >= 0, "ERC20Capped: Cap exceeded");
    super._mint(account, amount);
  }
}
