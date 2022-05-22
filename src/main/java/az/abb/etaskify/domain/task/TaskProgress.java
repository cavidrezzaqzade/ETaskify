package az.abb.etaskify.domain.task;

/**
 * @author caci
 * @since 23.05.2022
 */

public enum TaskProgress {
    ADDED("ADDED"),
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE");

    private final String name;

    TaskProgress(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static String getNameByOrdinal(Integer ordinal){
        return TaskProgress.values()[ordinal].getName();
    }

    public static Integer getOrdinalByName(String name){
        for (TaskProgress tp : TaskProgress.values()) {
            if(tp.getName().equalsIgnoreCase(name))
                return tp.ordinal();
        }
        return  null;
    }

}
