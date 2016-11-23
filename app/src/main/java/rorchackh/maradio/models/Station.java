package rorchackh.maradio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable {

    private int id;
    private String title;
    private String subtitle;
    private String link;
    private String imageLink;

    public Station() {
    }

    public Station(int id, String title, String subtitle, String link, String imageLink) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.link = link;
        this.imageLink = imageLink;
    }
    public String getTitle() {
        return title.trim();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle.trim();
    }

    public String getLink() {
        return link;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String toString() {
        if (!subtitle.trim().equalsIgnoreCase("")) {
            return String.format("%s - %s", title, subtitle);
        }

        return title;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Station)) {
            return false;
        }

        Station other = (Station) o;
        return other.getId() == getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(link);
        dest.writeString(imageLink);
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    public Station(Parcel in) {
        id = in.readInt();
        title = in.readString();
        subtitle = in.readString();
        link = in.readString();
        imageLink = in.readString();
    }

    public int getId() {
        return id;
    }
}
