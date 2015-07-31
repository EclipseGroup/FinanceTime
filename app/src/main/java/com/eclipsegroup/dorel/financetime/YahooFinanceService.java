package com.eclipsegroup.dorel.financetime;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class YahooFinanceService {

    private FinanceServiceCallback callback;
    private Context context;
    private Exception error;

    public YahooFinanceService(FinanceServiceCallback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    public void refreshQuote(String symbol) {

        new AsyncTask<String, Void, Quote>() {
            @Override
            protected Quote doInBackground(String... strings) {

                String symbol = strings[0];



                Quote quote= loadCache(symbol);

                if (quote != null) {
                    return quote;
                } else {
                    quote = new Quote();
                }


                String YQL = "select Symbol,Name,Open,DaysLow,DaysHigh,LastTradePriceOnly from yahoo.finance.quotes where symbol = \" " + symbol + "\"";

                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json&env=store://datatables.org/alltableswithkeys&callback=", Uri.encode(YQL));

                try {
                    URLEncoder.encode(endpoint, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject data = new JSONObject(result.toString());

                    JSONObject queryResults = data.optJSONObject("query");

                    int count = queryResults.optInt("count");

                    if (count == 0) {
                        error = new SymbolException("No information found for " + symbol);
                        return null;
                    }

                    JSONObject quoteJSON = queryResults.optJSONObject("results").optJSONObject("quote");

                    quote.populate(quoteJSON);

                    return quote;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Quote quote) {

                if (quote == null && error != null) {
                    callback.serviceFailure(error);
                } else {
                    callback.serviceSuccess(quote);
                }

            }

        }.execute(symbol);
    }

    private Quote loadCache(String location) {
        String CACHED_FINANCE_FILE = "finance.data";
        try {
            FileInputStream inputStream = context.openFileInput(CACHED_FINANCE_FILE);

            StringBuilder cache = new StringBuilder();
            int content;
            while ((content = inputStream.read()) != -1) {
                cache.append((char) content);
            }

            inputStream.close();

            JSONObject jsonCache = new JSONObject(cache.toString());

            Quote quote = new Quote();
            quote.populate(jsonCache);

            if (quote.getSymbol().equalsIgnoreCase(location)) {
                return quote;
            }

        } catch (Exception e) {
            context.deleteFile(CACHED_FINANCE_FILE);
        }

        return null;
    }

    public class SymbolException extends Exception {
        public SymbolException(String detailMessage) {
            super(detailMessage);
        }
    }
}
