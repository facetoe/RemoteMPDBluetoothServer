package org.a0z.mpdlocal;




public class UnknownAlbum extends Album {
    public static final UnknownAlbum instance = new UnknownAlbum();

    private UnknownAlbum() {
		super("Unknown", UnknownArtist.instance);
	}

	@Override
	public String subText() {
		return "";
	}
}
