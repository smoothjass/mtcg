import app.App;
import server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        Server server = new Server(app, 7777);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
docker run --rm --detach --name swe1db -e POSTGRES_USER=swe1user -e POSTGRES_PASSWORD=swe1pw -v data:/var/lib/postgresql/data -p 5431:5432 postgres
*/
