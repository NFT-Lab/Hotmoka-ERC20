package io.nfteam.nftlab.hotmoka.erc20;

import io.takamaka.code.lang.*;
import io.takamaka.code.math.UnsignedBigInteger;
import io.takamaka.code.util.StorageMap;
import io.takamaka.code.util.StorageTreeMap;

/**
 * Implementation of the {@link IERC20} interface.
 *
 * <p>This implementation is agnostic to the way tokens are created. This means that a supply
 * mechanism has to be added in a derived contract using {@link ERC20#_mint}. For a generic
 * mechanism see {@link ERC20PresetMinterPauser}.
 *
 * <p>TIP: For a detailed writeup see our guide
 * https://forum.zeppelin.solutions/t/how-to-implement-erc20-supply-mechanisms/226[How to implement
 * supply mechanisms].
 *
 * <p>We have followed general OpenZeppelin guidelines: functions revert instead of returning
 * `false` on failure. This behavior is nonetheless conventional and does not conflict with the
 * expectations of ERC20 applications.
 *
 * <p>Additionally, an {@link IERC20.Approval} event is emitted on calls to {@link
 * ERC20#transferFrom}. This allows applications to reconstruct the allowance for all accounts just
 * by listening to said events. Other implementations of the EIP may not emit these events, as it
 * isn't required by the specification.
 *
 * <p>Finally, the non-standard {@link ERC20#decreaseAllowance} and {@link ERC20#increaseAllowance}
 * functions have been added to mitigate the well-known issues around setting allowances. See
 * {IERC20-approve}.
 */
public abstract class ERC20 extends Contract implements IERC20 {

  private final StorageMap<Contract, UnsignedBigInteger> _balances = new StorageTreeMap<>();
  private final StorageMap<Contract, StorageMap<Contract, UnsignedBigInteger>> _allowances =
      new StorageTreeMap<>();

  private UnsignedBigInteger _totalSupply;
  private final boolean generateEvents;

  private String _name;
  private String _symbol;

  public ERC20(String name_, String symbol_) {
    this._name = name_;
    this._symbol = symbol_;
    this.generateEvents = false;
  }

  public ERC20(String name_, String symbol_, boolean generateEvents) {
    this._name = name_;
    this._symbol = symbol_;
    this.generateEvents = generateEvents;
  }

  @View
  public String name() {
    return _name;
  }

  @View
  public String symbol() {
    return _symbol;
  }

  /** @see IERC20#totalSupply() */
  @Override
  @View
  public UnsignedBigInteger totalSupply() {
    return _totalSupply;
  }

  /** @see IERC20#balanceOf(Contract) */
  @Override
  @View
  public UnsignedBigInteger balanceOf(Contract owner) {
    return _balances.getOrDefault(owner, new UnsignedBigInteger(0));
  }

  /** @see IERC20#transfer(Contract, UnsignedBigInteger) */
  @Override
  @FromContract
  public boolean transfer(Contract recipient, UnsignedBigInteger amount) {
    _transfer(caller(), recipient, amount);
    return true;
  }

  /** @see IERC20#allowance(Contract, Contract) */
  @Override
  public UnsignedBigInteger allowance(Contract owner, Contract spender) {
    return _allowances.get(owner).getOrDefault(spender, new UnsignedBigInteger(0));
  }

  /** @see IERC20#approve(Contract, UnsignedBigInteger) */
  @Override
  @FromContract
  public boolean approve(Contract spender, UnsignedBigInteger amount) {
    _approve(caller(), spender, amount);
    return true;
  }

  /** @see IERC20#transferFrom(Contract, Contract, UnsignedBigInteger) */
  @Override
  @FromContract
  public boolean transferFrom(Contract sender, Contract recipient, UnsignedBigInteger amount) {
    _transfer(caller(), recipient, amount);

    UnsignedBigInteger currentAllowance =
        _allowances.get(sender).getOrDefault(caller(), new UnsignedBigInteger(0));
    Takamaka.require(
        currentAllowance.compareTo(amount) >= 0, "ERC20: transfer amount exceeds allowance");
    _approve(sender, caller(), currentAllowance.subtract(amount));

    return true;
  }

  /**
   * Atomically increases the allowance granted to {@param spender} by the caller.
   *
   * <p>This is an alternative to {@link ERC20#approve} that can be used as a mitigation for
   * problems described in {IERC20-approve}.
   *
   * <p>Emits an {@link IERC20.Approval} event indicating the updated allowance.
   *
   * <p>Requirements:
   *
   * @param spender cannot be the zero address.
   */
  @FromContract
  public boolean increaseAllowance(Contract spender, UnsignedBigInteger addedValue) {
    _approve(
        caller(),
        spender,
        _allowances
            .getOrDefault(caller(), new StorageTreeMap<>())
            .getOrDefault(spender, new UnsignedBigInteger(0))
            .add(addedValue));
    return true;
  }

  /**
   * Atomically decreases the allowance granted to `spender` by the caller.
   *
   * <p>This is an alternative to {approve} that can be used as a mitigation for problems described
   * in {IERC20-approve}.
   *
   * <p>Emits an {Approval} event indicating the updated allowance.
   *
   * <p>Requirements:
   *
   * <p>- `spender` cannot be the zero address. - `spender` must have allowance for the caller of at
   * least `subtractedValue`.
   */
  @FromContract
  public boolean decreaseAllowance(Contract spender, UnsignedBigInteger subtractedValue) {
    UnsignedBigInteger currentAllowance =
        _allowances
            .getOrDefault(caller(), new StorageTreeMap<>())
            .getOrDefault(spender, new UnsignedBigInteger(0));
    Takamaka.require(
        currentAllowance.compareTo(subtractedValue) >= 0, "ERC20: decreased allowance below zero");
    _approve(caller(), spender, currentAllowance.subtract(subtractedValue));
    return true;
  }

  /**
   * Moves `amount` of tokens from `sender` to `recipient`.
   *
   * <p>This internal function is equivalent to {transfer}, and can be used to e.g. implement
   * automatic token fees, slashing mechanisms, etc.
   *
   * <p>Emits a {Transfer} event.
   *
   * <p>Requirements:
   *
   * <p>- `sender` cannot be the zero address. - `recipient` cannot be the zero address. - `sender`
   * must have a balance of at least `amount`.
   */
  private void _transfer(Contract sender, Contract recipient, UnsignedBigInteger amount) {
    Takamaka.require(sender != null, "ERC20: transfer from null address");
    Takamaka.require(recipient != null, "ERC20: transfer to null address");

    _beforeTokenTransfer(sender, recipient, amount);

    UnsignedBigInteger senderBalance = _balances.getOrDefault(sender, new UnsignedBigInteger(0));
    UnsignedBigInteger recipientBalance =
        _balances.getOrDefault(recipient, new UnsignedBigInteger(0));
    Takamaka.require(
        senderBalance.compareTo(amount) >= 0, "ERC20: transfer amount exceeds balance");
    _balances.put(
        sender, _balances.getOrDefault(sender, new UnsignedBigInteger(0)).subtract(amount));
    _balances.put(recipient, recipientBalance.add(amount));

    event(new Transfer(sender, recipient, amount));

    _afterTokenTransfer(sender, recipient, amount);
  }

  /**
   * Creates {@code amount} tokens and assigns them to {@code amount}, increasing the total supply.
   *
   * <p>Emits a {@link ERC20.Transfer} event with {@code from} set to the zero address.
   *
   * @param account account to min amount to
   * @param amount amount of tokens to mint
   */
  @FromContract
  protected void _mint(Contract account, UnsignedBigInteger amount) {
    Takamaka.require(account != null, "ERC20: minting to null address");
    _beforeTokenTransfer(null, account, amount);

    _totalSupply.add(amount);
    _balances.put(account, _balances.getOrDefault(account, new UnsignedBigInteger(0)).add(amount));

    event(new Transfer(null, account, amount));

    _afterTokenTransfer(null, account, amount);
  }

  /**
   * Destroys {@code amount} tokens from {@code account}, reducing the total supply.
   *
   * <p>Emits a {@link ERC20.Transfer} event with {@code to} set to the zero address.
   *
   * @param account account to burn tokens from, cannot be null
   * @param amount amount of tokens to burn
   */
  protected void _burn(Contract account, UnsignedBigInteger amount) {
    Takamaka.require(account != null, "ERC20: burn from the null address");

    _beforeTokenTransfer(account, null, amount);

    UnsignedBigInteger accountBalance = _balances.getOrDefault(account, new UnsignedBigInteger(0));
    Takamaka.require(accountBalance.compareTo(amount) >= 0, "ERC20: burn amount exceeds balance");
    _balances.put(account, accountBalance.subtract(amount));

    _totalSupply = _totalSupply.subtract(amount);

    event(new Transfer(account, null, amount));

    _afterTokenTransfer(account, null, amount);
  }

  /**
   * Sets `amount` as the allowance of `spender` over the `owner` s tokens.
   *
   * <p>This internal function is equivalent to `approve`, and can be used to e.g. set automatic
   * allowances for certain subsystems, etc.
   *
   * <p>Emits an {Approval} event.
   *
   * <p>Requirements:
   *
   * <p>- `owner` cannot be the zero address. - `spender` cannot be the zero address.
   */
  protected void _approve(Contract owner, Contract spender, UnsignedBigInteger amount) {
    Takamaka.require(owner != null, "ERC20: approve from the null address");
    Takamaka.require(spender != null, "ERC20: approve to the null address");

    _allowances.getOrDefault(owner, new StorageTreeMap<>()).put(spender, amount);

    event(new Approval(owner, spender, amount));
  }

  /**
   * Hook that is called before any transfer of tokens. This includes minting and burning.
   *
   * <p>Calling conditions:
   *
   * <p>- when `from` and `to` are both non-zero, `amount` of ``from``'s tokens will be transferred
   * to `to`. - when `from` is zero, `amount` tokens will be minted for `to`. - when `to` is zero,
   * `amount` of ``from``'s tokens will be burned. - `from` and `to` are never both zero.
   *
   * <p>To learn more about hooks, head to xref:ROOT:extending-contracts.adoc#using-hooks[Using
   * Hooks].
   */
  private void _beforeTokenTransfer(Contract from, Contract to, UnsignedBigInteger amount) {}

  /**
   * Hook that is called before any transfer of tokens. This includes minting and burning.
   *
   * <p>Calling conditions:
   *
   * <p>- when `from` and `to` are both non-zero, `amount` of ``from``'s tokens will be transferred
   * to `to`. - when `from` is zero, `amount` tokens will be minted for `to`. - when `to` is zero,
   * `amount` of ``from``'s tokens will be burned. - `from` and `to` are never both zero.
   *
   * <p>To learn more about hooks, head to xref:ROOT:extending-contracts.adoc#using-hooks[Using
   * Hooks].
   */
  private void _afterTokenTransfer(Contract from, Contract to, UnsignedBigInteger amount) {}

  /**
   * Generates the given event if events are allowed for this token.
   *
   * @param event the event to generate
   */
  protected final void event(Event event) {
    if (generateEvents) {
      Takamaka.event(event);
    }
  }
}
