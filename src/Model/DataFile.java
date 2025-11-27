package Model;
import enums.*;

public class DataFile {
private String nameEntity;
private ETypeFile typeFile;

public DataFile(String nameEntity, ETypeFile typeFile) {
    this.nameEntity = nameEntity;
    this.typeFile = typeFile;
}

public String getNameEntity() {
    return nameEntity;
}
public ETypeFile getTypeFile() {
    return typeFile;
}


}
