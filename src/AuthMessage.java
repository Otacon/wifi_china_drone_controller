/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class AuthMessage extends Message {

    @Override
    byte getTypeFlag() {
        return 0x52;
    }

    @Override
    public String toString() {
        return "Auth\n" + super.toString();
    }
}
