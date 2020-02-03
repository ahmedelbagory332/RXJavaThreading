package com.example.bego.rxjavathreading;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //rx java threading

        /*Observable.just(1,2,3,4,5)
                .doOnNext(c-> Log.d(TAG, "aly upstream: "+c + " current thread "+ Thread.currentThread().getName()))
                .subscribeOn(Schedulers.computation())// to change thread
                .subscribe(o-> Log.d(TAG, "aly downstream: "+o + " current thread "+ Thread.currentThread().getName()));
   */

        EditText text = findViewById(R.id.edittext);

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {

                text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(charSequence.length() != 0)
                        emitter.onNext(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }
        }).doOnNext(c -> Log.d(TAG, "aly upstream: " + c))
//                .map(new Function<Object, Object>() {
//                    @Override
//                    public Object apply(Object o) throws Exception {
//                        return Integer.parseInt(o.toString())*2 ;
//                    }
//                })
               // .debounce(2, TimeUnit.SECONDS)
                //.distinctUntilChanged()
               // .filter(c -> !c.toString().equals("ahmed"))
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return sendDataToApi(o.toString());
                    }
                })
                .subscribe(o ->{
                    Log.d(TAG, "aly downstream: " + o);

                });


    }


    public void sleep(int i) throws InterruptedException {
        Thread.sleep(3000);
    }

    @SuppressLint("CheckResult")
    public ObservableSource<?> sendDataToApi(String data){

        Observable observable = Observable.just("calling api 1 to send " + data);
        observable.subscribe(c -> Log.d(TAG, "aly sendDataToApi: " + c));

        return observable;
    }

}
