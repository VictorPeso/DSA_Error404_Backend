package edu.upc.dsa;

import edu.upc.dsa.exceptions.FailedLoginException;
import edu.upc.dsa.models.User;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class TracksManagerImpl implements TracksManager {
    private static TracksManager instance;
    protected List<User> users;
    final static Logger logger = Logger.getLogger(TracksManagerImpl.class);

    private TracksManagerImpl() {
        this.users = new LinkedList<>();
    }

    public static TracksManager getInstance() {
        if (instance==null) instance = new TracksManagerImpl();
        return instance;
    }

    public int size() {
        int ret = this.users.size();
        logger.info("size " + ret);

        return ret;
    }

    public User addTrack(User t) {
        logger.info("new Track " + t);

        this.users.add (t);
        logger.info("new Track added");
        return t;
    }

    public User addTrack(String title, String singer){
        return this.addTrack(null, title, singer);
    }

    public User addTrack(String id, String title, String singer) {
        return this.addTrack(new User(id, title, singer));
    }

    public User getTrack(String id) {
        logger.info("getTrack("+id+")");

        for (User t: this.users) {
            if (t.getId().equals(id)) {
                logger.info("getTrack("+id+"): "+t);

                return t;
            }
        }

        logger.warn("not found " + id);
        return null;
    }

    public User getTrack2(String id) throws FailedLoginException {
        User t = getTrack(id);
        if (t == null) throw new FailedLoginException();
        return t;
    }


    public List<User> findAll() {
        return this.users;
    }

    @Override
    public void deleteTrack(String id) {

        User t = this.getTrack(id);
        if (t==null) {
            logger.warn("not found " + t);
        }
        else logger.info(t+" deleted ");

        this.users.remove(t);

    }

    @Override
    public User updateTrack(User p) {
        User t = this.getTrack(p.getId());

        if (t!=null) {
            logger.info(p+" rebut!!!! ");

            t.setSinger(p.getSinger());
            t.setTitle(p.getTitle());

            logger.info(t+" updated ");
        }
        else {
            logger.warn("not found "+p);
        }

        return t;
    }

    public void clear() {
        this.users.clear();
    }
}