
package Modules;

import Exceptions.DBProviderException;
import Exceptions.NonExistingElementException;
import Network.User;
import data.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class CollectionService {
    protected static Long elementsCount = 0L;
    private Date lastInitDate;
    protected static LinkedList<MusicBand> collection;
    private CompareMusicBands comparator;
    private ReentrantLock locker;


    public CollectionService() {
        collection = new LinkedList<>();
        this.lastInitDate = new Date();
        this.comparator = new CompareMusicBands();
        this.locker = new ReentrantLock();
    }

    private class CompareMusicBands implements Comparator<MusicBand>{

        @Override
        public int compare(MusicBand b1, MusicBand b2) {
            if (b1.getEstablishmentDate().isAfter(b2.getEstablishmentDate())) return 1;
            if (b1.getEstablishmentDate().isEqual(b2.getEstablishmentDate())) return 0;
            else return -1;
        }

        @Override
        public Comparator<MusicBand> reversed() {
            return Comparator.super.reversed();
        }
    }

    public LinkedList<MusicBand> add(MusicBandRaw element) throws DBProviderException {
        locker.lock();
        if (DBProvider.addMusicBand(element)){
            ++elementsCount;
            MusicBand newBand = new MusicBand(
                Integer.valueOf(elementsCount.toString()), 
                element.getName(), element.getCoordinates(), 
                lastInitDate, 
                element.getNumberOfParticipants(), 
                element.getEstablishmentDate(), 
                element.getMusicGenre(), 
                element.getStudio(), 
                element.getUser().getUsername());
            collection.add(newBand);
            locker.unlock();
            return new LinkedList<>();
        }
        locker.unlock();
        throw new DBProviderException("Произошла ошибка при добавлении элемента.");
    }

    public String info(){
        return "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + lastInitDate + "\n"
                + "Количество элементов: " + collection.size();
    }

    public LinkedList<MusicBand> show(){
        return sortByEstablishmentDate(collection);
    }

    public LinkedList<MusicBand> update(User user, int current_id, MusicBandRaw element) throws DBProviderException {
        locker.lock();
        try {
            if (DBProvider.updateMusicBand(user, current_id, element)) {

                for (MusicBand musicBand : collection) {
                    if (current_id == musicBand.getId() && musicBand.getOwner().equals(user.getUsername())) {
                        collection.remove(musicBand);

                        MusicBand newElement = new MusicBand(
                                (int) current_id,
                                element.getName(),
                                element.getCoordinates(),
                                new Date(),
                                element.getNumberOfParticipants(),
                                element.getEstablishmentDate(),
                                element.getMusicGenre(),
                                element.getStudio(),
                                element.getUser().getUsername()
                        );
                        collection.add(newElement);
                        break;
                    }
                }
                return new LinkedList<>();
            }
            throw new DBProviderException("Произошла ошибка во время изменения элемента");
        } finally {
            locker.unlock();
        }
    }

    public MusicBand getByIndex(int index) {
        return collection.get(index);
    }

    public LinkedList<MusicBand> removeByIndex(User user, int index) throws DBProviderException {
        locker.lock();
        try {
            if(DBProvider.removeMusicBandById(getByIndex(index).getId())) {
                collection.removeIf(musicBand -> musicBand.getId() == getByIndex(index).getId());
                return new LinkedList<>();
            }
            throw new DBProviderException("Произошла ошибка при удалении элемента. Возможно вы вышли из предела листа.");
        } finally {
            locker.unlock();
        }
    }

    public LinkedList<String> printFieldDescendingEstablishmentDate() {
        Collection<MusicBand> copy = new TreeSet<>(Collections.reverseOrder(MusicBand::compareToEstablishmentDate));
        LinkedList<String> establishmentDates = new LinkedList<>();
        copy.addAll(collection);
        copy.forEach(musicBand -> establishmentDates.add(musicBand.getEstablishmentDate().toString()));
        return establishmentDates;
    }

    public LinkedList<MusicBand> removeById(User user, int id) throws DBProviderException {
        locker.lock();
        try {
            if (DBProvider.removeMusicBandById(id)){
                collection.removeIf(musicBand -> musicBand.getId() == id);
                return new LinkedList<>();
            }
            throw new DBProviderException("Произошла ошибка при удалении элемента. Возможно элемента с таким id не существует");
        } finally {
            locker.unlock();
        }
    }

    public LinkedList<MusicBand> clear(User user) throws DBProviderException {
        locker.lock();
        try {
            if (DBProvider.clearMusicBands(user)){
                collection.removeIf(vehicle -> vehicle.getOwner().equals(user.getUsername()));
                return sortByEstablishmentDate(collection);
            }
            throw new DBProviderException("произошла ошибка при добавлении элемента");
        } finally {
            locker.unlock();
        }
    }

    public LinkedList<MusicBand> shuffle() {
        Collections.shuffle(collection);
        return collection;
    }

    public String groupCountingByEstablishmentDate() {
        Map<LocalDateTime, Long> establishmentDateMap = collection.stream().collect(Collectors.groupingBy(MusicBand::getEstablishmentDate, Collectors.counting()));
        return establishmentDateMap.toString();
    }

    public LinkedList<MusicBand> filterLessThanNumberOfParticipants(Long numberOfParticipants) throws NonExistingElementException {
        var filteredCollection = collection.stream().filter(musicBand -> musicBand.getNumberOfParticipants() < (numberOfParticipants)).collect(Collectors.toCollection(LinkedList::new));

        if (filteredCollection.isEmpty()){
            throw new NonExistingElementException("Элементов с таким именем не существует.");
        }
        return sortByNumberOfParticipants(filteredCollection);
    }

    private synchronized LinkedList<MusicBand> sortByNumberOfParticipants(LinkedList<MusicBand> collection) {
        return collection.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
    }

    private synchronized LinkedList<MusicBand> sortByEstablishmentDate(LinkedList<MusicBand> collection){
        return collection.stream().sorted(comparator).collect(Collectors.toCollection(LinkedList::new));
    }
}