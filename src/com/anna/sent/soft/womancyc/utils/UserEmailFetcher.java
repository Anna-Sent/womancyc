package com.anna.sent.soft.womancyc.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * This class uses the AccountManager to get the primary email address of the
 * current user.
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class UserEmailFetcher {
	private static final String INVALID_EMAIL = "";

	/**
	 * 
	 * @param context
	 * @return email or empty string
	 */
	public static String getEmail(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = getAccount(accountManager);

			if (account == null) {
				return INVALID_EMAIL;
			} else {
				return account.name;
			}
		} else {
			return INVALID_EMAIL;
		}
	}

	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}

		return account;
	}
}
