package edu.hitsz.user;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



import android.content.Context;

import edu.hitsz.application.Settings;

public class UserDaoImpl implements UserDao, Serializable {
    private List<User> users;
    Context context;
    public static String fileName="datum.txt";
    public UserDaoImpl(Context context) {
        this.context = context;
        users = new ArrayList<>();
        switch (Settings.difficulty){
            case Casual: fileName="text1.txt";break;
            case Medium: fileName="text2.txt";break;
            case Hard:   fileName="text3.txt";break;
            default:
                throw new IllegalStateException("Unexpected value: " + Settings.difficulty);
        }
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.users = (List<User>)ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }



    @Override
    public List<User> getAllUsers() {
        Collections.sort(users, (o1, o2) -> {
            if(o1.score!= o2.score){
                return o2.score-o1.score;
            }
            else{
                return o1.date.compareTo(o2.date);
            }
        });
        //对排序后的结果，使用遍历去设置排名
        int rank=1;
        for(int i=0; i<users.size();i++){
            User user = users.get(i);
            user.setRank(rank);
            rank++;
        }
        return users;
    }

    @Override
    public User findUser(int userID) {
        return users.stream().filter(e->e.userID==userID).findAny().orElse(null);
    }

    @Override
    public void addUser(User user) throws IOException {
        users.add(user);

        FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);

        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(users);
        oos.close();
    }

    @Override
    public void deleteUser(int rank) throws IOException {
        int size = users.size();
        for(int i = size-1;i>=0;i--){
            User targetUser = users.get(i);
            if(targetUser.getRank() == rank){
                users.remove(targetUser);
            }
        }
        FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(users);
        oos.close();
    }

//    @Override
//    public void readFromFile() {
//        String PATH="saves/save0.data";
////        switch (Settings.difficulty){
////            case Casual: PATH=PATH+"save0.data";break;
////            case Medium: PATH=PATH+"save1.data";break;
////            case Hard:   PATH=PATH+"save2.data";break;
////            default:
////                throw new IllegalStateException("Unexpected value: " + Settings.difficulty);
////        }
//        try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(PATH))){
//            userDao=(UserDaoImpl) ois.readObject();
//        }catch(Exception err) {
//            err.printStackTrace();
//        }
//        if(userDao==null) userDao=new UserDaoImpl();
//    }
//
//    @Override
//    public void writeToFile() {
//        String PATH="saves/save0.data";
////        switch (Settings.difficulty){
////            case Casual: PATH=PATH+"save0.data";break;
////            case Medium: PATH=PATH+"save1.data";break;
////            case Hard:   PATH=PATH+"save2.data";break;
////            default:
////                throw new IllegalStateException("Unexpected value: " + Settings.difficulty);
////        }
//        try {
//            FileOutputStream fileOutputStream = context.openFileOutput("easy.txt",Context.MODE_APPEND);
//            System.out.println("我要下班！");ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
//
//            oos.writeObject(userDao);
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
}
