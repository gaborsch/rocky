/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import rockstar.parser.Line;

/**
 * Denotes a runtime exception while executing a parsed Rockstar program
 *
 * @author Gabor
 */
public class RockstarRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1312636143538274940L;
	
	private final List<RockstarStacktraceLine> stacktrace = new LinkedList<>();

    public RockstarRuntimeException(String message) {
        super(message);
    }

    public void addStacktraceLine(Line l, BlockContext ctx) {
        stacktrace.add(new RockstarStacktraceLine(l, ctx));
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        RockstarStacktraceLine savedLine = null;
        int repeatCount = 0;
        for (RockstarStacktraceLine stl : stacktrace) {
            if (savedLine != null) {
                if (savedLine.equals(stl)) {
                    repeatCount++;
                } else {
                    appendLine(sb, savedLine, repeatCount);
                    savedLine = stl;
                    repeatCount = 0;
                }
            } else {
                savedLine = stl;
            }
        }

        if (savedLine != null) {
            appendLine(sb, savedLine, repeatCount);
        }

        return sb.toString();
    }

    private void appendLine(StringBuilder sb, RockstarStacktraceLine stl, int repeatCount) {
        Line l = stl.getLine();
        BlockContext ctx = stl.getContext();
        sb.append("\n  ")
                .append(l.getFileName())
                .append(":")
                .append(l.getLnum())
                .append(" (")
                .append(ctx.getName())
                .append("): ")
                .append(l.getOrigLine());
        if (repeatCount > 1) {
            sb.append("\n   [+ ")
                    .append(repeatCount)
                    .append(" more time")
                    .append(repeatCount > 1 ? "s" : "")
                    .append("]");
        }
    }

    private static class RockstarStacktraceLine {

        private final Line line;
        private final BlockContext context;

        public RockstarStacktraceLine(Line line, BlockContext context) {
            this.line = line;
            this.context = context;
        }

        public Line getLine() {
            return line;
        }

        public BlockContext getContext() {
            return context;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.line);
            hash = 31 * hash + Objects.hashCode(this.context);
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
            final RockstarStacktraceLine other = (RockstarStacktraceLine) obj;
            if (!Objects.equals(this.line, other.line)) {
                return false;
            }
            if (!Objects.equals(this.context, other.context)) {
                return false;
            }
            return true;
        }

    }

}
