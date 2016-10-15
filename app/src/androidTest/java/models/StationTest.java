package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rorchackh.maradio.models.Station;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StationTest {

    Station station;

    @Before
    public void setUp() throws Exception {
        // this.station = new Station("Hit Radio", "100% R&B", "http://rnb.ice.infomaniak.ch/rnb-128.mp3", false);
    }

    @After
    public void tearDown() throws Exception {
        // this.station = null;
    }

    @Test
    public void getThumbnail() throws Exception {
        // assertEquals(this.station.getThumbnail(), "hit_radio_100_r_b");
    }

    @Test
    public void equals() throws Exception {
        // Todo: Fix these tests
//        Station newStation = new Station("", "", "http://rnb.ice.infomaniak.ch/rnb-128.mp3", false);
//
//        assertFalse(this.station.equals(null));
//        assertTrue(this.station.equals(newStation));
    }

}