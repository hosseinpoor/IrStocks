package ir.irse.stocks.other;

import android.support.annotation.NonNull;

public class StockItem {

    private String symbol;
    private String name;
    private String market;
    private String market_cap;
    private String percentage;
    private String price;
    private String price_change;

    public StockItem(@NonNull String symbol , String name , String market , String market_cap , String percentage , String price , String price_change){

        this.symbol = symbol;
        this.name = name;
        this.market = market;
        this.market_cap = market_cap;
        this.percentage = percentage;
        this.price = price;
        this.price_change = price_change;
    }


    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setMarket(String market){
        this.market = market;
    }
    public void setMarketCap(String market_cap){
        this.market_cap = market_cap;
    }
    public void setPercentage(String percentage){
        this.percentage = percentage;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public void setPriceChange(String price_change){
        this.price_change = price_change;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getMarket() {
        return market;
    }

    public String getMarketCap() {
        return market_cap;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getPrice() {
        return price;
    }

    public String getPriceChange() {
        return price_change;
    }
}
