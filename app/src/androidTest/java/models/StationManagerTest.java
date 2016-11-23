package models;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rorchackh.maradio.models.Station;
import rorchackh.maradio.receivers.StationManagerReceiver;

@RunWith(AndroidJUnit4.class)
public class StationManagerTest extends InstrumentationTestCase {
    private Context mContext;

    @Before
    public void setUp() throws Exception {
        this.mContext = InstrumentationRegistry.getTargetContext();
        StationManagerReceiver.init(mContext);
        Thread.sleep(2000);
    }

    @After
    public void tearDown() throws Exception {
        this.mContext = null;
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(20, StationManagerReceiver.getStations(mContext).size());
    }

    @Test
    public void testGetFirst() throws Exception {
        Station first = new Station(0, "", "", "http://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3", "");
        assertEquals(first, StationManagerReceiver.getFirst(mContext));
    }

}