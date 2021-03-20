package rockstar.statement;

/**
 * Marker interface for block start elements
 *
 * @author Gabor
 */
public interface ContinuingBlockStatementI {

    public boolean appendTo(Block finishedBlock);

}
