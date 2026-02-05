package pl.przemekzagorski.training.patterns.decorator;

/**
 * Bazowy interfejs statku - może być dekorowany.
 */
public interface Ship {

    /**
     * @return nazwa statku
     */
    String getName();

    /**
     * @return opis statku z wszystkimi ulepszeniami
     */
    String getDescription();

    /**
     * @return całkowity koszt statku z ulepszeniami
     */
    int getCost();

    /**
     * @return siła ognia (armaty + dodatki)
     */
    int getFirepower();
}
