package Modules;

import Network.User;
import data.*;
import java.sql.*;
import java.time.ZoneId;
import java.util.Date;

public class DBProvider {
    private static Connection connection;

    public static void establishConnection(String url, String user, String password){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkUserExistence(String username){

        String query = "SELECT EXISTS(SELECT 1 FROM USER_TABLE WHERE USERNAME = ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            ResultSet res = p.executeQuery();
            if (res.next()){
                return res.getBoolean(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean checkUserPassword(User user){
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "SELECT SAFE_PASSWORD FROM USER_TABLE WHERE USERNAME = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            ResultSet res = p.executeQuery();

            if (res.next()){
                String storedHashedPassword = res.getString("SAFE_PASSWORD");
                return storedHashedPassword.equals(hashedPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void addUser(User user){
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "INSERT INTO USER_TABLE (USERNAME, SAFE_PASSWORD) VALUES (?, ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            p.setString(2, hashedPassword);
            p.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() {
        CollectionService.elementsCount = loadElCount();

        String query = "SELECT MUSIC_BANDS.ID, MUSIC_BANDS.BAND_NAME, COORDINATES.X, COORDINATES.Y, MUSIC_BANDS.CREATION_DATE, MUSIC_BANDS.NUMBER_OF_PARTICIPANTS," +
                "MUSIC_BANDS.ESTABLISHMENT_DATE, MUSIC_BANDS.MUSIC_GENRE, STUDIO.ADDRESS, USER_TABLE.USERNAME FROM MUSIC_BANDS JOIN USER_TABLE ON USER_TABLE.ID = MUSIC_BANDS.OWNER_ID " + 
                "JOIN COORDINATES ON COORDINATES.ID = MUSIC_BANDS.COORDINATES_FK JOIN STUDIO ON STUDIO.ID = MUSIC_BANDS.STUDIO_FK";

        try (PreparedStatement p = connection.prepareStatement(query)){
            ResultSet res = p.executeQuery();
            while (res.next()){
                try {
                    var element = new MusicBand(
                            (Integer) res.getInt(1),
                            res.getString(2), 
                            new Coordinates(res.getFloat(3), res.getFloat(4)),
                            res.getDate(5),
                            (Long) res.getLong(6),
                            res.getTimestamp(7).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            MusicGenre.valueOf(res.getString(8)),
                            new Studio(res.getString(9)),
                            res.getString(10)
                    );
                    if (checkUserExistence(element.getOwner())){
                        CollectionService.collection.add(element);
                    }

                } catch (IllegalArgumentException e){
                    System.out.println("Повреждённый атрибут типа у элемента с id " + res.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int addCoordinates(Coordinates coordinates) {
        String query = "INSERT INTO COORDINATES (X, Y) VALUES (?, ?) RETURNING ID";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setFloat(1, coordinates.getX());
            preparedStatement.setFloat(2, coordinates.getY());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static int addStudio(Studio studio) {
        String query = "INSERT INTO STUDIO(ADDRESS) VALUES (?) RETURNING ID";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, studio.getAddress());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static boolean addMusicBand(MusicBandRaw musicBandRaw){

        String query = "INSERT INTO MUSIC_BANDS (name, coordinates_fk, creation_date, number_of_participants, establishment_date, music_genre, studio_fk, owner_id)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT id FROM USER_TABLE WHERE username = ?))";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, musicBandRaw.getName());
            int coordinates_id = addCoordinates(musicBandRaw.getCoordinates());
            long dateInMilliseconds = new Date().getTime();
            p.setTimestamp(3, new Timestamp(dateInMilliseconds));
            if(((Long) musicBandRaw.getNumberOfParticipants()).equals(null)) {
                p.setNull(4, Types.BIGINT);
            } else {
                p.setLong(4, musicBandRaw.getNumberOfParticipants());
            }
            long establishmentDateMill = musicBandRaw.getEstablishmentDate().getLong(null);
            if(((Long) establishmentDateMill).equals(null)) {
                p.setNull(5, Types.TIMESTAMP);
            } else {
                p.setTimestamp(5, new Timestamp(establishmentDateMill));
            }
            if(musicBandRaw.getMusicGenre() == null) {
                p.setNull(6, Types.VARCHAR);
            } else {
                p.setString(6, musicBandRaw.getMusicGenre().getMusicGenre());
            }
            p.setInt(7, addStudio(musicBandRaw.getStudio()));
            p.setString(8, musicBandRaw.getUser().getUsername());
            p.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int updateCoordinates(Coordinates coordinates, Integer id) {
        String query = "UPDATE COORDINATES SET X = ?, Y = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setFloat(1, coordinates.getX());
            if(((Float) coordinates.getY()).equals(null)) {
                preparedStatement.setNull(2, Types.FLOAT);
            } else {
                preparedStatement.setFloat(2, coordinates.getY());
            }
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static int updateStudio(Studio studio, Integer id) {
        String query = "UPDATE STUDIO SET ADDRESS = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, studio.getAddress());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    public static boolean updateMusicBand(User user, int id, MusicBandRaw musicBandRaw){

        String query = "UPDATE MUSIC_BANDS SET name = ?, coordinates_fk = ?, number_of_participants = ?, establishment_date = ?," +
                " music_genre = ?::music_genre, studio_fk = ? WHERE (id = ? AND owner_id IN (SELECT id FROM user_TABLE WHERE username = ?))";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, musicBandRaw.getName());
            updateCoordinates(musicBandRaw.getCoordinates(), id);
            p.setInt(2, id);
            if(((Long) musicBandRaw.getNumberOfParticipants()).equals(null)) {
                p.setNull(3, Types.BIGINT);
            } else {
                p.setLong(3, musicBandRaw.getNumberOfParticipants());
            }
            long establishmentDateMill = musicBandRaw.getEstablishmentDate().getLong(null);
            if(((Long) establishmentDateMill).equals(null)) {
                p.setNull(4, Types.TIMESTAMP);
            } else {
                p.setTimestamp(4, new Timestamp(establishmentDateMill));
            }
            if(musicBandRaw.getMusicGenre() == null) {
                p.setNull(5, Types.VARCHAR);
            } else {
                p.setString(5, musicBandRaw.getMusicGenre().getMusicGenre());
            }
            updateStudio(musicBandRaw.getStudio(), id);
            p.setInt(6, id);
            p.setInt(7, id);
            p.setString(8, user.getUsername());

            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeMusicBandById(int id){

        String query = "DELETE FROM MUSIC_BANDS WHERE id = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setInt(1, id);
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean clearMusicBands(User user){

        String query = "DELETE FROM MUSIC_BANDS WHERE OWNER_ID IN (SELECT id FROM user_TABLE WHERE username = ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, user.getUsername());
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long loadElCount(){
        String query = "select last_value from MUSIC_BANDS_id_seq";

        try (PreparedStatement p = connection.prepareStatement(query)){
            ResultSet res = p.executeQuery();

            if (res.next()){
                return res.getLong(1);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
