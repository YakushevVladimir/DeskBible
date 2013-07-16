package com.BibleQuote.ui;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.BibleQuoteService;
import com.BibleQuote.R;
import com.BibleQuote.utils.ViewUtils;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 12.07.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */


public class ServiceActivity extends SherlockFragmentActivity {

	private String toModuleID = "";

	public final static String BROADCAST_ACTION = "com.BibleQuote.ui.ServiceActivity";

	public final static String STATUS_MSG = "StatusMsg";

	public final static int STATUS_INFO = 100;
	public final static String PASS_NUMBER = "PassNumber";
	public final static String FROM_MODULE_ID = "fromModuleID";
	public final static String TO_MODULE_ID = "toModuleID";
	public final static String BOOK_ID = "BookID";
	public final static String BOOK_NUMBER = "BookNumber";
	public final static String BOOKS_QTY = "BooksQty";

	public final static int STATUS_FINISH = 200;

	private BroadcastReceiver broadcastReceiver;

	private boolean isBound = false;
	private ServiceConnection serviceConnection;
	private Intent intentService;
	private BibleQuoteService bqService;

	private TextView tvPassNum;
	private TextView tvFromTo;
	private TextView tvCurrBook;
	private TextView tvWillUpd;

	private int iPassNumber = 0;
	private String sFromModule = "";
	private String sToModule = "";
	private String sBook = "";
	private int iBookNumber = 0;
	private int iBooksQty = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.service);
		ViewUtils.setActionBarBackground(this);

		tvPassNum = (TextView) findViewById(R.id.textView_passnum);
		tvPassNum.setText(String.format(getString(R.string.service_passnum), String.valueOf(iPassNumber)));

		tvFromTo = (TextView) findViewById(R.id.textView_fromto);
		tvFromTo.setText(String.format(getString(R.string.service_fromto), sFromModule, sToModule));

		tvCurrBook = (TextView) findViewById(R.id.textView_currbook);
		tvCurrBook.setText(String.format(getString(R.string.service_currbook),
				  sBook, String.valueOf(iBookNumber), String.valueOf(iBooksQty)));

		tvWillUpd = (TextView) findViewById(R.id.textView_willupd);
		tvWillUpd.setText(getString(R.string.service_willupd));


		// создаем BroadcastReceiver
		broadcastReceiver = new BroadcastReceiver() {

			// действия при получении сообщений
			public void onReceive(Context context, Intent intent) {
				int parameter = intent.getIntExtra(STATUS_MSG, 0);

				// Ловим сообщения об окончании задачи
				switch (parameter) {

					case STATUS_INFO:
						iPassNumber = intent.getIntExtra(PASS_NUMBER, 0);
						sFromModule = intent.getStringExtra(FROM_MODULE_ID);
						sToModule = intent.getStringExtra(TO_MODULE_ID);
						sBook = intent.getStringExtra(BOOK_ID);
						iBookNumber = intent.getIntExtra(BOOK_NUMBER, 0);
						iBooksQty = intent.getIntExtra(BOOKS_QTY, 0);


						tvPassNum.setText(String.format(getString(R.string.service_passnum), String.valueOf(iPassNumber)));

						tvFromTo.setText(String.format(getString(R.string.service_fromto), sFromModule, sToModule));

						tvCurrBook.setText(String.format(getString(R.string.service_currbook),
								  sBook, String.valueOf(iBookNumber), String.valueOf(iBooksQty)));

						tvWillUpd.setText(" \n ");

						break;

					case STATUS_FINISH:
						if (isBound) {
							unbindService(serviceConnection);
							isBound = false;
						}
						startActivity(new Intent(ServiceActivity.this, ReaderActivity.class));
						finish();
						break;

					default:
						break;
				}
			}
		};


		// создаем фильтр для BroadcastReceiver
		IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
		// регистрируем (включаем) BroadcastReceiver
		registerReceiver(broadcastReceiver, intFilt);


		toModuleID = getIntent().getStringExtra(TO_MODULE_ID);
		intentService = new Intent(this, BibleQuoteService.class)
				  .putExtra(TO_MODULE_ID, toModuleID);


		serviceConnection = new ServiceConnection() {

			public void onServiceConnected(ComponentName name, IBinder binder) {
				if (binder == null) {
					bqService = null;
					isBound = false;
				} else {
					bqService = ((BibleQuoteService.LocalBinder) binder).getService();
					isBound = true;
				}
			}

			public void onServiceDisconnected(ComponentName name) {
				isBound = false;
			}
		};


		if (toModuleID != null) {
			startService(intentService);
		}
	}


	@Override
	protected void onStart() {
		super.onStart();

		bindService(intentService, serviceConnection, 0);
	}


	@Override
	protected void onStop() {
		super.onStop();

		if (isBound) {
			unbindService(serviceConnection);
			isBound = false;
		}
	}


	@Override
	protected void onDestroy() {

		// дерегистрируем (выключаем) BroadcastReceiver
		unregisterReceiver(broadcastReceiver);


		if (isBound) {
			unbindService(serviceConnection);
			isBound = false;
		}


		super.onDestroy();
	}


	public void onClickStop(View v) {

		if (isBound) {
			unbindService(serviceConnection);
			isBound = false;
		}

		stopService(intentService);


		BibleQuoteApp app = (BibleQuoteApp) getApplication();
		app.RestartInit();
		startActivity(new Intent(this, ReaderActivity.class));


		finish();
	}

}
