import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MassageList {
    private final List<String> massages = new ArrayList<>();

    public void addMassage (String massage) {
        massages.add(massage);
    }

    public List<String> getMassages() {
        return Collections.unmodifiableList(massages);
    }
}
