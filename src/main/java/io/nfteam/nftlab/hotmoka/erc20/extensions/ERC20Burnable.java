package io.nfteam.nftlab.hotmoka.erc20.extensions;

import io.nfteam.nftlab.hotmoka.erc20.ERC20;
import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.lang.Takamaka;
import io.takamaka.code.math.UnsignedBigInteger;

public abstract class ERC20Burnable extends ERC20 {
  public ERC20Burnable(String name_, String symbol_) {
    super(name_, symbol_);
  }

  public ERC20Burnable(String name_, String symbol_, boolean generateEvents) {
    super(name_, symbol_, generateEvents);
  }

  /**
   * Destroys {@code amount} tokens from the caller, see {@link ERC20#_burn}
   * @param amount amount of token to burn
   */
  @FromContract
  public void burn(UnsignedBigInteger amount) {
    this._burn(caller(), amount);
  }

  /**
   * Destroys {@code amount} tokens from {@code account}, deducting from the caller's
   * allowance.
   *
   * @see ERC20#_burn
   * @see ERC20#allowance
   */
  @FromContract
  public void burnFrom(Contract account, UnsignedBigInteger amount) {
    UnsignedBigInteger currentAllowance = this.allowance(account, caller());
    Takamaka.require(currentAllowance.compareTo(amount) >= 0, "ERC20: burn amount exceeds allowance");
    this._approve(account, caller(), currentAllowance.subtract(amount));
    this._burn(account, amount);
  }
}
