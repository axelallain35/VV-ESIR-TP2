package fr.istic.vv;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if(!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());
        PublicElementsPrinter printer = new PublicElementsPrinter();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(printer, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        // Initializing detectedFields for the exercice
        String detectedFields = "";

        // Iterating variable for class names
        int i = 0;

        // Looping through all private variables without getter
        for (FieldDeclaration field : printer.getPrivateVarWithoutGetter()) {
            detectedFields += 
            
            // Adding private variable name
            "Private variable : " + field.getVariable(0).getName() +

            // Adding class name of each variable
             " | Class : " + printer.getClassNames().get(i)+

            // Adding package name
            " | Package name : " + printer.getPackageNames()  +"\n";
            i++;
        }

        

          // Writing txt file
          try {

            // Instance of file writer
            FileWriter myWriter = new FileWriter("noGetter.txt");

            // Writing to file
            myWriter.write(detectedFields);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        
    }


}
