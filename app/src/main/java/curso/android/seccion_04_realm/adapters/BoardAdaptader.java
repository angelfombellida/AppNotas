package curso.android.seccion_04_realm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import curso.android.seccion_04_realm.R;
import curso.android.seccion_04_realm.models.Board;

public class BoardAdaptader extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Board> boards;

    public BoardAdaptader(Context context, int layout, List<Board> boards) {
        this.context = context;
        this.layout = layout;
        this.boards = boards;
    }

    @Override
    public int getCount() {
        return boards.size();
    }

    @Override
    public Board getItem(int i) {
        return boards.get(i);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_board_item, null);
            vh = new ViewHolder();
            vh.textViewTitle = convertView.findViewById(R.id.textViewBoardTitle);
            vh.textViewNotes = convertView.findViewById(R.id.textViewBoardNotes);
            vh.textViewDate = convertView.findViewById(R.id.textViewBoardDate);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        Board board = boards.get(i);

        vh.textViewTitle.setText(board.getTitle());

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String createdAt = df.format(board.getCreatedAt());
        vh.textViewDate.setText(createdAt);

        int numberOfNotes = board.getNotes().size();
        String textNotes = "" + numberOfNotes;
        if(numberOfNotes == 1) textNotes+= " note";
        else textNotes+=" notes";
        vh.textViewNotes.setText(textNotes);

        return convertView;
    }

    private class ViewHolder {
        public TextView textViewTitle;
        public TextView textViewNotes;
        public TextView textViewDate;
    }

}
