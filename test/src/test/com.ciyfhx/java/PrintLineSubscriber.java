package com.ciyfhx.java;

import java.util.concurrent.Flow;

public class PrintLineSubscriber implements Flow.Subscriber<String>{

    private Flow.Subscription subscription;
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        System.out.format("Subscribe to %s\n", subscription.toString());
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        System.out.format("Message: %s\n", item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("Done");
    }
}
