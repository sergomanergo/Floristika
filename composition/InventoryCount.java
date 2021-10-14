package ru.kazachkov.florist.composition;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.util.Pair;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.tools.Utils;
import rx.Observable;

/**
 * Created by ishmukhametov on 19.10.16.
 */

public class InventoryCount extends BaseObservable {

    private String goodsName;
    private BigDecimal price;
    private List<BigDecimal> terms;
    private BigDecimal count = BigDecimal.ZERO;


    public InventoryCount(String goodsName, BigDecimal price, List<BigDecimal> terms) {
        this.goodsName = goodsName;
        this.price = price;
        this.terms = terms;
        updateCount();
    }

    @Bindable
    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void showImage(View view) {
        Utils.toast(view.getContext(), "Show image");
    }

    @Bindable
    public String getCount() {
        return count.toString();
    }

    public void setCount(String count) {
        if (!TextUtils.equals(count, this.count.toString())) {
            BigDecimal newCount;
            try {
                newCount = new BigDecimal(count);
            } catch (NumberFormatException e) {
                newCount = BigDecimal.ZERO;
            }
            this.count = newCount;
            this.terms.clear();
            terms.add(newCount);
            notifyPropertyChanged(BR.termsString);
            notifyPropertyChanged(BR.termsSpannable);
        }
    }

    @Bindable
    public String getPrice() {
        return price.toString();
    }

    @Bindable
    public String getTermsString() {
        if (terms.size() == 0) {
            return "0 = 0";
        }
        return Observable.just(count).map(BigDecimal::toString)
                .zipWith(Observable.from(terms).map(BigDecimal::toString).reduce((s, s2) -> s + " + " + s2), ((s, s2) -> s + " = " + s2))
                .toBlocking()
                .first();
    }

    @Bindable
    public SpannableStringBuilder getTermsSpannable() {
        if (terms.size() == 0) {
            return new SpannableStringBuilder("0 = 0");
        }
        return Observable.just(count).map(BigDecimal::toString)
                .zipWith(Observable.just(terms), Pair::create)
                .flatMap(stringListPair -> {
                    SpannableStringBuilder spannableString = new SpannableStringBuilder(stringListPair.first + " = ");
                    return Observable.from(stringListPair.second)
                            .map(bigDecimal -> {
                                CharSequence charSequence = bigDecimal.toString();
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View view) {
                                        terms.remove(bigDecimal);
                                        notifyPropertyChanged(BR.count);
                                        notifyPropertyChanged(BR.termsSpannable);
                                        notifyPropertyChanged(BR.termsString);
                                    }
                                };
                                int length = 0;
                                if (spannableString.charAt(spannableString.length() - 2) != '=') {
                                    length = spannableString.append(" + ").length();
                                } else {
                                    length = spannableString.length();
                                }
                                spannableString.append(charSequence);
                                TextPaint ds = new TextPaint();
                                ds.linkColor = 0xffeeeeee;
                                clickableSpan.updateDrawState(ds);
                                spannableString.setSpan(clickableSpan, length, length + charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                return spannableString;
                            }).last();
                })
                .toBlocking()
                .first();
    }

    private static class ReduceSpannable {

        private final ClickableSpan clickableSpan;
        private final CharSequence charSequence;

        ReduceSpannable(ClickableSpan clickableSpan, CharSequence charSequence) {
            this.clickableSpan = clickableSpan;
            this.charSequence = charSequence;
        }
    }


    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }

    public TextView.OnEditorActionListener getOnEditorActionListener() {
        return (textView, id, keyEvent) -> {
            boolean handled = false;
            if (id == EditorInfo.IME_ACTION_DONE) {
                try {
                    BigDecimal value = new BigDecimal(textView.getText().toString());
                    terms.add(value);
                } catch (NumberFormatException e) {
                    terms.add(BigDecimal.ZERO);
                }
                updateCount();
                notifyPropertyChanged(BR.termsString);
                notifyPropertyChanged(BR.termsSpannable);
                notifyPropertyChanged(BR.count);
                textView.setText("");
                handled = true;
            }
            return handled;
        };
    }

    List<BigDecimal> getTerms() {
        return terms;
    }

    private void updateCount() {
        if (terms.size() == 0) {
            count = BigDecimal.ZERO;
        } else {
            count = Observable.from(terms).reduce(BigDecimal::add).toBlocking().first();
        }
    }


}
