package de.hsaugsburg.sharegame.shares;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import de.hsaugsburg.sharegame.assets.Share;
import de.hsaugsburg.sharegame.shares.exceptions.ShareAlreadyExistsException;
import de.hsaugsburg.sharegame.shares.exceptions.UnknownShareException;
import de.hsaugsburg.sharegame.timer.SingleTimer;

/**
 * Important: Classes who extend this Class must call startUpdate in the constructor if
 * the rates should be updated periodically.
 */
public abstract class StockPriceProvider implements StockPriceInfo {
	private final Map<String,Share> shares;
	
	
	public StockPriceProvider(Share[] shares) {
		this.shares = new HashMap<String,Share>();
	}

	@Override
	public long getShareValue(String name) {
		return getShare(name).getValue();
	}

	@Override
	public String getAllShares() {
		StringBuilder out = new StringBuilder();
		shares.forEach((String str, Share s)->out.append(s.getName() + " " + s.getValue() + ", "));
		return out.toString();
	}
	
	protected void startUpdate(long delay, long period) {
		SingleTimer.getTimer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
               updateShareRates();
            }
        }, delay, period);
	}
	
	protected final void updateShareRates() {
		shares.forEach((String str, Share s)->updateShareRate(s));
	}
	
	protected abstract void updateShareRate(Share share);
	
	@Override
	public String[] getShareNames() {
		return (String[]) shares.keySet().toArray();
			
	}
	
	public void addShare(String name, long value) {
		if(shares.get(name) != null ){
			throw new ShareAlreadyExistsException(name);
		}
		Share share = new Share(name,value);
		shares.put(name, share);
		updateShareRate(share);
	}
	
	public Share getShare(String name)  {
		Share share = shares.get(name);
		if(share == null)
			throw new UnknownShareException(name);
		return share;	
	}
}
