package controller;

/**
 * Main method to execute, run in two model: graphic or not
 * Invoke runAllWithGraphic() or runAllWithOutGraphic()
 */
public class Execute {
    public static void main(String[] args) {
        String fileName = "info.data";
        boolean graphic = true;
        if (args.length == 2) {
            fileName = args[0];
            if(args[1].equals("f")){
                graphic = false;
            }
        }
        Controller controller = new Controller(fileName);
        if(graphic){
            controller.runAllWithGraphic();
        } else {
            controller.runAllWithOutGraphic();
        }
    }
}
