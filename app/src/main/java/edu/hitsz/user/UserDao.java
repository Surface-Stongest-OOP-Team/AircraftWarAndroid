package edu.hitsz.user;

import java.io.IOException;
import java.util.List;

public interface UserDao {
    public List<User> getAllUsers() ;

    public User findUser(int userID);
    public void addUser(User user) throws IOException;
    public void deleteUser(int rank) throws IOException;
//    public void readFromFile();
//    public void writeToFile();
}
