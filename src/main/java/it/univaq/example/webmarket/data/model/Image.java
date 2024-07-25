package it.univaq.example.webmarket.data.model;

import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItem;
import java.io.InputStream;

public interface Image extends DataItem<Integer> {

    String getCaption();

    void setCaption(String caption);

    InputStream getImageData() throws DataException;

    void setImageData(InputStream is) throws DataException;

    String getImageType();

    void setImageType(String type);

    long getImageSize();

    void setImageSize(long size);

    public String getFilename();

    public void setFilename(String imageFilename);

}