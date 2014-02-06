package org.a0z.mpdlocal;




import java.util.Locale;

public class Artist extends Item  {
	public static String singleAlbumFormat="%1 Album";
	public static String multipleAlbumsFormat="%1 Albums";

	private final String name;
	private final String sort;
	//private final boolean isVa;
	private final int albumCount;
    private boolean isAlbumArtist;

    public Artist(String name, int albumCount) {
        this(name, albumCount, false);
    }

    public Artist(String name, int albumCount, boolean isAlbumArtist) {
		this.name=name;
		if (null != name && name.toLowerCase(Locale.getDefault()).startsWith("the ")) {
			sort=name.substring(4);
		} else {
			sort=null;
		}
		this.albumCount=albumCount;
                this.isAlbumArtist = isAlbumArtist;
    }

    public Artist(String name) {
        this(name, 0, false);
    }

    public Artist(String name, boolean isAlbumArtist) {
        this(name, 0, isAlbumArtist);

    }

    public Artist(Artist a) {
        this.name = a.name;
        this.albumCount = a.albumCount;
        this.sort = a.sort;
        this.isAlbumArtist = a.isAlbumArtist;
    }

	public String getName() {
		return name;
	}

    public boolean isAlbumArtist() {
        return isAlbumArtist;
    }
    public void setIsAlbumArtist(boolean aa){
        isAlbumArtist = aa;
    }

	public String sort() {
        return null == sort ? name == null ? "" : name : sort;
    }

	@Override
	public String subText() {
		if (0==albumCount) {
			return null;
		}

		return String.format(1==albumCount ? singleAlbumFormat : multipleAlbumsFormat, albumCount);
	}

	@Override
    public int compareTo(Item o) {
		if (o instanceof Artist) {
			Artist oa=(Artist)o;
			/*
			if (isVa && !oa.isVa) {
				return -1;
			}
			if (!isVa && oa.isVa) {
				return 1;
			}
			*/
			return sort().compareToIgnoreCase(oa.sort());
		}

		return super.compareTo(o);
	}

    @Override
    public boolean equals(Object o) {
    	return (o instanceof Artist) && ((Artist)o).name.equals(name);
    }

    public boolean isSameOnList(Item o) {
        if (null == o) {
            return false;
        }
        return (name.equals(((Artist)o).name));
    }

    public String info() {
        return getName() + (isAlbumArtist()?" (AA)":"");
    }

}
