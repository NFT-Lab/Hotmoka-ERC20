package io.nfteam.nftlab.hotmoka.erc20;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.Event;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC20 {
  /** Returns the amount of tokens in existence. */
  @View
  UnsignedBigInteger totalSupply();

  /** Returns the amount of tokens owned by (@code account). */
  @View
  UnsignedBigInteger balanceOf(Contract owner);

  /**
   * Moves `amount` tokens from the caller's account to `recipient`.
   *
   * <p>Returns a boolean value indicating whether the operation succeeded.
   *
   * <p>Emits a {Transfer} event.
   */
  boolean transfer(Contract recipient, UnsignedBigInteger amount);

  /**
   * Returns the remaining number of tokens that {@param spender} will be allowed to spend on behalf
   * of {@param owner} through {@link IERC20#transferFrom}. This is zero by default.
   *
   * <p>This value changes when {@link IERC20#approve} or {@link IERC20#transferFrom} are called.
   */
  @View
  UnsignedBigInteger allowance(Contract owner, Contract spender);

  /**
   * Sets {@param amount} as the allowance of {@param spender} over the caller's tokens.
   *
   * <p>Returns a boolean value indicating whether the operation succeeded.
   *
   * <p>IMPORTANT: Beware that changing an allowance with this method brings the risk that someone
   * may use both the old and the new allowance by unfortunate transaction ordering. One possible
   * solution to mitigate this race condition is to first reduce the spender's allowance to 0 and
   * set the desired value afterwards:
   * https://github.com/ethereum/EIPs/issues/20#issuecomment-263524729
   *
   * <p>Emits an {@link Approval} event.
   */
  boolean approve(Contract spender, UnsignedBigInteger amount);

  /**
   * Moves `amount` tokens from `sender` to `recipient` using the allowance mechanism. `amount` is
   * then deducted from the caller's allowance.
   *
   * <p>Returns a boolean value indicating whether the operation succeeded.
   *
   * <p>Emits a {Transfer} event.
   */
  boolean transferFrom(Contract sender, Contract recipient, UnsignedBigInteger amount);

  /**
   * Emitted when (@code value) tokens are moved from one account (@code from) to another (@code
   * to).
   *
   * <p>Note that (@code value) may be zero.
   */
  class Transfer extends Event {
    public final Contract from;
    public final Contract to;
    public final UnsignedBigInteger value;

    @FromContract
    Transfer(Contract from, Contract to, UnsignedBigInteger value) {
      this.from = from;
      this.to = to;
      this.value = value;
    }
  }

  /** Emitted when (@code owner) enables (@code approved) to manage the (@code tokenId) token. */
  class Approval extends Event {
    public final Contract owner;
    public final Contract approved;
    public final UnsignedBigInteger value;

    @FromContract
    public Approval(Contract owner, Contract approved, UnsignedBigInteger value) {
      this.owner = owner;
      this.approved = approved;
      this.value = value;
    }
  }
}
