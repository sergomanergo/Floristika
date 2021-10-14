package ru.kazachkov.florist.logic;


import android.content.Context;
import android.text.TextUtils;

import java.net.UnknownHostException;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.tools.ui.Dialogs;
import rx.functions.Action1;

public abstract class Error {

    public static Action1<Throwable> onError(Context context) {
        return throwable -> handle(context, throwable);
    }

    public static Action1<Throwable> onError() {
        return Throwable::printStackTrace;
    }

    public static Action1<Throwable> onError(Context context, Action1<Throwable> action1) {
        return throwable -> {
            handle(context, throwable);
            action1.call(throwable);
        };
    }

    public static void handle(Context context, Throwable throwable) {
        if (TextUtils.isEmpty(throwable.getMessage())) {
            throwable.printStackTrace();
            Utils.toast(context, R.string.error_happen);
        } else {
            if (throwable instanceof UnknownHostException) {
                throwable.printStackTrace();
                Dialogs.alertDialog(context, context.getString(R.string.server_connect_throwable)).show();
            } else {
                throwable.printStackTrace();
                Dialogs.alertDialog(context, throwable.getMessage()).show();
            }

        }
    }
}
