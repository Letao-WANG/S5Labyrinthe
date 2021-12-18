package controller;

/**
 * Main method to execute, run in two model: graphic or not
 * Invoke runAllWithGraphic() or runAllWithOutGraphic()
 */
public class Execute {
    public static void main(String[] args) {
        Controller controller = new Controller("myInfo.data");
        controller.runAllWithGraphic();
//        controller.runAllWithOutGraphic();
    }
}
