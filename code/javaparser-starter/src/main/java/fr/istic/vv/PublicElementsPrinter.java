package fr.istic.vv;

import java.util.ArrayList;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    // Initializing arraylist for private variables without getter
    private ArrayList<FieldDeclaration> privateVarWithoutGetter = new ArrayList<>();

    // Initializing arraylist for getters
    private ArrayList<MethodDeclaration> getters = new ArrayList<>();

    // Initializing arraylist for class names
    private ArrayList<String> classNames = new ArrayList<>();

    // Initializing arraylist for package names
    private ArrayList<String> packageNames = new ArrayList<>();

    // Getters
    public ArrayList<String> getPackageNames() {return packageNames;}
    public ArrayList<String> getClassNames() {return classNames;}
    public ArrayList<MethodDeclaration> getGetters() {return getters;}
    public ArrayList<FieldDeclaration> getPrivateVarWithoutGetter() {return privateVarWithoutGetter;}

    @Override
    public void visit(CompilationUnit unit, Void arg) { 

        // Looping through all methods
        for(MethodDeclaration method : unit.findAll(MethodDeclaration.class)) {
                if(method.getNameAsString().startsWith("get")){
                    getters.add(method);
                }
            }

        // Looping through all package names
        for(PackageDeclaration pack : unit.findAll( PackageDeclaration.class)) {
            packageNames.add(pack.getNameAsString());
        }

        // Looping through all private variables without getter
        for(TypeDeclaration<?> type : unit.getTypes()) {
            for(FieldDeclaration field : type.getFields()) {

                // Checking if variable is private
                if(field.isPrivate()){

                    // Looping through all methods
                    for(MethodDeclaration method : type.getMethods()){

                        // Checking if method is getter
                        if(!(method.getNameAsString().toLowerCase().equals("get"+field.getVariable(0).getNameAsString().toLowerCase()))){
                            
                            // Adding private variable without getter to arraylist
                            privateVarWithoutGetter.add(field);
                            classNames.add(type.getNameAsString());
                        }
                    }
                }
            }
            
        }
       
            
        }

    

    

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if(!declaration.isPublic()) return;
        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        // Printing nested types in the top level
        for(BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration)
                member.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        if(!declaration.isPublic()) return;
        System.out.println("  " + declaration.getDeclarationAsString(true, true));
    }

}
