package org.a0z.mpdlocal;



public class UnknownArtist extends Artist {

    public static final UnknownArtist instance = new UnknownArtist();

    private UnknownArtist() {
        super("Unknown", 0);
    }

    @Override
    public String subText() {
        return "";
    }
}
