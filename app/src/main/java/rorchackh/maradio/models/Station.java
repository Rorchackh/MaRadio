package rorchackh.maradio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable {

    private int id;
    private String title;
    private String subtitle;
    private String link;
    private String imageLink;
    private boolean favorite;

    private boolean active;

    public Station() {
    }

    public Station(int id, String title, String subtitle, String link, String imageLink, boolean favorite, boolean active) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.link = link;
        this.imageLink = imageLink;
        this.favorite = favorite;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public String getTitle() {
        return title.trim();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
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

    public Boolean isFavorite() {
        return favorite;
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
        dest.writeInt(favorite ? 1 : 0);
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
        favorite = in.readInt() == 1;
    }

    public int getId() {
        return id;
    }
}
