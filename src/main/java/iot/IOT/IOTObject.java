package iot.IOT;

public class IOTObject {
    private String objectType;
    private String roomNo;
    private String building;
    private String color;
    private int objectId;
    private int functionId;

    public IOTObject(String objectType, String roomNo, String building, String color, int object_id, int functionId){
        this.objectType = objectType;
        this.roomNo = roomNo;
        this.building = building;
        this.color = color;
        this.objectId = object_id;
        this.functionId = functionId;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

    public String getObjectType() {

        return objectType;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getBuilding() {
        return building;
    }

    public String getColor() {
        return color;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getFunctionId() {
        return functionId;
    }


}
