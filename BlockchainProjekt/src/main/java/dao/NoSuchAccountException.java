package dao;

public class NoSuchAccountException extends Exception {
	private static final long serialVersionUID = 1L;

	private long accountId;

	public NoSuchAccountException(long accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "NoSuchAccountException [account=" + accountId + "]";
	}

}
