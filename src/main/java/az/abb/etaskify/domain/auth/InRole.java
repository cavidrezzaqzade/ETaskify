package az.abb.etaskify.domain.auth;

public enum InRole {
    ADMIN("ADMIN"), USER("USER");

    private final String role;

    InRole(String roleName){
        this.role = roleName;
    }

    public String getRole(){
        return role;
    }

}
