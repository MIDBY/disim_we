package it.univaq.example.webmarket.data.dao.impl;

import it.univaq.example.webmarket.data.dao.ImageDAO;
import it.univaq.example.webmarket.data.model.Category;
import it.univaq.example.webmarket.data.model.Image;
import it.univaq.example.webmarket.data.model.impl.ImageImpl;
import it.univaq.example.webmarket.data.model.impl.proxy.ImageProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.OptimisticLockException;

public class ImageDAO_MySQL extends DAO implements ImageDAO {

    private PreparedStatement sImageByID, sImages, sImageByCategory, iImage, uImage;

    public ImageDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //precompiliamo tutte le query utilizzate nella classe
            //precompile all the queries uses in this class
            sImageByID = connection.prepareStatement("SELECT * FROM immagine WHERE id=?");
            sImages = connection.prepareStatement("SELECT * FROM immagine");
            sImageByCategory = connection.prepareStatement("SELECT id_immagine FROM categoria WHERE id=?");
            iImage = connection.prepareStatement("INSERT INTO immagine (titolo,tipo,nome_file,grandezza) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uImage = connection.prepareStatement("UPDATE immagine SET titolo=?,tipo=?,nome_file=?,grandezza=?,versione=? WHERE id=? and versione=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing webshop data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //anche chiudere i PreparedStamenent � una buona pratica...
        //also closing PreparedStamenents is a good practice...
        try {
            sImageByID.close();
            sImageByCategory.close();
            iImage.close();

        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        super.destroy();
    }

    @Override
    public Image createImage() {
        return new ImageProxy(getDataLayer());
    }

    //helper    
    private ImageProxy createImage(ResultSet rs) throws DataException {
        ImageProxy i = (ImageProxy)createImage();
        try {
            i.setKey(rs.getInt("id"));
            i.setCaption(rs.getString("titolo"));
            i.setImageSize(rs.getLong("grandezza"));
            i.setImageType(rs.getString("tipo"));
            i.setFilename(rs.getString("nome_file"));
            i.setVersion(rs.getLong("versione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create image object form ResultSet", ex);
        }
        return i;
    }

    @Override
    public Image getImage(int image_key) throws DataException {
        Image i = null;
        //prima vediamo se l'oggetto è già stato caricato
        //first look for this object in the cache
        if (dataLayer.getCache().has(Image.class, image_key)) {
            i = dataLayer.getCache().get(Image.class, image_key);
        } else {
            //altrimenti lo carichiamo dal database
            //otherwise load it from database
            try {
                sImageByID.clearParameters();
                sImageByID.setInt(1, image_key);
                try (ResultSet rs = sImageByID.executeQuery()) {
                    if (rs.next()) {
                        i = createImage(rs);
                        //e lo mettiamo anche nella cache
                        //and put it also in the cache
                        dataLayer.getCache().add(Image.class, i);

                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load image by ID", ex);
            }
        }
        return i;
    }

    @Override
    public List<Image> getImages() throws DataException {
        List<Image> result = new ArrayList<Image>();
        try (ResultSet rs = sImages.executeQuery()) {
            while (rs.next()) {
                result.add(getImage(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load images", ex);
        }
        return result;
    }

    @Override
    public Image getImage(Category category) throws DataException {
        Image result = new ImageImpl();
        try {
            sImageByCategory.setInt(1, category.getKey());
            try (ResultSet rs = sImageByCategory.executeQuery()) {
                while(rs.next()) {
                    result = getImage(rs.getInt("id_immagine"));
                }
            }
        } catch (SQLException e) {
            throw new DataException("Unable to load image by category", e);
        }
        return result;
    }

    @Override
    public void setImage(Image image) throws DataException {
                try {
            if (image.getKey() != null && image.getKey() > 0) { //update
                //non facciamo nulla se l'oggetto è un proxy e indica di non aver subito modifiche
                //do not store the object if it is a proxy and does not indicate any modification
                if (image instanceof DataItemProxy && !((DataItemProxy) image).isModified()) {
                    return;
                }
                uImage.setString(1, image.getCaption());
                uImage.setString(2, image.getImageType());
                uImage.setString(3, image.getFilename());
                uImage.setLong(4, image.getImageSize());

                long current_version = image.getVersion();
                long next_version = current_version + 1;

                uImage.setLong(5, next_version);
                uImage.setInt(6, image.getKey());
                uImage.setLong(7, current_version);

                if (uImage.executeUpdate() == 0) {
                    throw new OptimisticLockException(image);
                } else {
                    image.setVersion(next_version);
                }
            } else { //insert
                iImage.setString(1, image.getCaption());
                iImage.setString(2, image.getImageType());
                iImage.setString(3, image.getFilename());
                iImage.setLong(4, image.getImageSize());

                if (iImage.executeUpdate() == 1) {
                    //per leggere la chiave generata dal database
                    //per il record appena inserito, usiamo il metodo
                    //getGeneratedKeys sullo statement.
                    //to read the generated record key from the database
                    //we use the getGeneratedKeys method on the same statement
                    try ( ResultSet keys = iImage.getGeneratedKeys()) {
                        //il valore restituito è un ResultSet con un record
                        //per ciascuna chiave generata (uno solo nel nostro caso)
                        //the returned value is a ResultSet with a distinct record for
                        //each generated key (only one in our case)
                        if (keys.next()) {
                            //i campi del record sono le componenti della chiave
                            //(nel nostro caso, un solo intero)
                            //the record fields are the key componenets
                            //(a single integer in our case)
                            int key = keys.getInt(1);
                            //aggiornaimo la chiave in caso di inserimento
                            //after an insert, uopdate the object key
                            image.setKey(key);
                            //inseriamo il nuovo oggetto nella cache
                            //add the new object to the cache
                            dataLayer.getCache().add(Image.class, image);
                        }
                    }
                }
            }

            if (image instanceof DataItemProxy) {
                ((DataItemProxy) image).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store image", ex);
        }
    }
}