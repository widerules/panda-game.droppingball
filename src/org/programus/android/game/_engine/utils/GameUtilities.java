package org.programus.android.game._engine.utils;

import org.programus.android.game.R;
import org.programus.android.game._engine.MainActivity;

import android.content.Context;
import android.content.Intent;

public class GameUtilities {
	public static void ExitApplication(Context context) {
		Intent exitIntent = new Intent(context, MainActivity.class); 
		exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		exitIntent.putExtra(context.getString(R.string.appExit), true); 
		context.startActivity(exitIntent); 
	}
}
