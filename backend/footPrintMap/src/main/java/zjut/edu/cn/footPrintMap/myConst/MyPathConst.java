package zjut.edu.cn.footPrintMap.myConst;

public class MyPathConst {
    //项目根目录
    private final static String rootPath = System.getProperty("user.dir");

    private final static String avatarPath = rootPath + "/uploads/avatar";

    private final static String travelImagePath = rootPath + "/uploads/travelImage";

    public static String getAvatarPath() {
        return avatarPath;
    }

    public static String getTravelImagePath() {
        return travelImagePath;
    }
}
