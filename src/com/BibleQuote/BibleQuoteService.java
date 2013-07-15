package com.BibleQuote;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.BibleQuote.activity.ServiceActivity;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.utils.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 11.07.13
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */


public class BibleQuoteService extends Service {

	private static final String TAG = "BibleQuoteService";

	private Librarian myLibrarian;
	private String toModuleID;
	private boolean isRunning = false;

	private Thread thread = null;
	private LocalBinder localBinder = new LocalBinder();


	@Override
	public void onCreate() {
		super.onCreate();

		//android.os.Debug.waitForDebugger();

		BibleQuoteApp app = (BibleQuoteApp) getApplication();
		myLibrarian = app.getLibrarian();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//android.os.Debug.waitForDebugger();

		if (!isRunning) {
			isRunning = true;

			toModuleID = intent.getStringExtra(ServiceActivity.TO_MODULE_ID);

			sendNotification();
			RunTask();
		}

		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}


	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}


	@Override
	public boolean onUnbind(Intent intent) {
		//return super.onUnbind(intent);
		return true;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		//android.os.Debug.waitForDebugger();

		if (thread != null) {
			thread.interrupt();
		}
	}


	public class LocalBinder extends Binder {
		public BibleQuoteService getService() {
			return BibleQuoteService.this;
		}
	}


	private void sendNotification() {

		//android.os.Debug.waitForDebugger();

		// 1-я часть
		Notification notif = new Notification(
				  R.drawable.icon,
				  getString(R.string.service_notif_title),
				  System.currentTimeMillis());

		// 3-я часть
		Intent intentNotif = new Intent(this, ServiceActivity.class);
		//intent.putExtra(ReaderActivity.FILE_NAME, "somefile");
		PendingIntent pIntentNotif = PendingIntent.getActivity(this, 0, intentNotif, 0);

		// 2-я часть
		notif.setLatestEventInfo(this,
				  getString(R.string.service_notif_title),
				  String.format(getString(R.string.service_notif_text), myLibrarian.getCurrModule().ShortName, toModuleID),
				  pIntentNotif);

		// ставим флаг, чтобы уведомление пропало после нажатия
		//notif.flags |= Notification.FLAG_AUTO_CANCEL;

		// отправляем
		//notificationManager.notify(1, notif);
		startForeground(1, notif);
	}


	private void RunTask() {
		//android.os.Debug.waitForDebugger();

		thread = new Thread(new Runnable() {
			public void run() {
				try {

					if (toModuleID != null) {
						Log.i(TAG, String.format("Check of Versification Map from Current Module to moduleID=%1$s", toModuleID));

						myLibrarian.CheckVersificationMap(toModuleID);

						Intent intentResult = new Intent(ServiceActivity.BROADCAST_ACTION)
								  .putExtra(ServiceActivity.STATUS_MSG, ServiceActivity.STATUS_FINISH);
						sendBroadcast(intentResult);

						stopSelf();
					}

				} catch (BookNotFoundException e) {
					// TODO e.printStackTrace()

					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				} catch (OpenModuleException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}
		});

		thread.start();
	}

}
