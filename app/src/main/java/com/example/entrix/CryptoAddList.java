package com.example.entrix;

import java.util.ArrayList;

public class CryptoAddList {


    public void CoinsAdd(ArrayList<String> coins){
        coins.add("BTCUSDT");
        coins.add("ETHUSDT");
        coins.add("SOLUSDT");
        coins.add("AAVEUSDT");
        coins.add("AVAXUSDT");
        coins.add("PNUTUSDT");
        coins.add("IMXUSDT");
        coins.add("FARTCOINUSDT");
        coins.add("SUIUSDT");
        coins.add("XRPUSDT");
        coins.add("NEARUSDT");
        coins.add("HYPEUSDT");
        coins.add("LINKUSDT");
        coins.add("1000PEPEUSDT");
        coins.add("POPCATUSDT");
        coins.add("DOGEUSDT");
        coins.add("SEIUSDT");
    }
    public int getCoinIcon(String coin){
        switch (coin) {
            case "BTCUSDT": return R.drawable.btc;
            case "ETHUSDT": return R.drawable.eth;
            case "SOLUSDT": return R.drawable.sol;
            case "AAVEUSDT": return R.drawable.aave;
            case "AVAXUSDT": return R.drawable.avax;
            case "PNUTUSDT": return R.drawable.pnut;
            case "IMXUSDT": return R.drawable.imx;
            case "FARTCOINUSDT": return R.drawable.fartcoin;
            case "SUIUSDT": return R.drawable.sui;
            case "XRPUSDT": return R.drawable.xrp;
            case "NEARUSDT": return R.drawable.near;
            case "HYPEUSDT": return R.drawable.hype;
            case "LINKUSDT": return R.drawable.link;
            case "1000PEPEUSDT": return R.drawable.pepe;
            case "POPCATUSDT": return R.drawable.popcat;
            case "DOGEUSDT": return R.drawable.doge;
            case "SEIUSDT": return R.drawable.sei;

            default: return R.drawable.btc;
        }
    }
}
