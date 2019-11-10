/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Objects;

/**
 *
 * @author Gabor
 */
public class QualifiedClassName {
    
    private final PackagePath path;
    private final String name;

    public QualifiedClassName(PackagePath path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PackagePath getPath() {
        return path;
    }
    
    public String getFormattedFilename() {
        return path + "/" + PackagePath.formatPathPartName(name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.path);
        hash = 23 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QualifiedClassName other = (QualifiedClassName) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return path + "/" + name;
    }
    
}
