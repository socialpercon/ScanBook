package com.scanbook.view.activity;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.scanbook.R;
import com.scanbook.bean.Book;
import com.scanbook.common.Share2Weibo;
import com.scanbook.common.Share2Weixin;
import com.scanbook.net.BaseAsyncHttp;
import com.scanbook.net.HttpResponseHandler;
import com.scanbook.view.PromotedActionsLibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * <a href="http://fangjie.sinaapp.com">http://fangjie.sinaapp.com</a>
 * @version 1.0
 * @author JayFang
 * @describe 图书信息显示Activity
 */

public class BookViewActivity extends Activity {


    private TextView mTvRate,mTvPrice,mTvAuthor,mTvPublisher,mTvDate,mTvIsbn,mTvSummary,mTvPage,mTvtags,mTvContent;
    private ImageView mIvIcon;

    private Book mBook;

    private RelativeLayout mRlAnnotation;
    private String isbn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_bookview);
        initBtn();
        findViews();
        if(getIntent().hasExtra("book")){
            mBook=(Book)getIntent().getParcelableExtra("book");
            updateToView();
        }else if(getIntent().hasExtra("isbn")){

            isbn=getIntent().getStringExtra("isbn");
            getRequestData(isbn);
        }

        mRlAnnotation=(RelativeLayout)findViewById(R.id.rl_review);
        mRlAnnotation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        Intent intent=new Intent(BookViewActivity.this,AnnotationListActivity.class);
                        intent.putExtra("id",mBook.getId());
                        startActivity(intent);
                    }
                }, 1000);

            }
        });

    }

    private void findViews(){
        mTvAuthor=(TextView)findViewById(R.id.tv_book_author);
        mTvPublisher=(TextView)findViewById(R.id.tv_book_publicer);
        mTvDate=(TextView)findViewById(R.id.tv_book_time);
        mTvIsbn=(TextView)findViewById(R.id.tv_book_isbn);
        mTvRate=(TextView)findViewById(R.id.tv_book_score);
        mTvPrice=(TextView)findViewById(R.id.tv_book_price);
        mTvPage=(TextView)findViewById(R.id.tv_book_page);
        mTvtags=(TextView)findViewById(R.id.tv_book_tag);
        mIvIcon=(ImageView)findViewById(R.id.iv_book_icon);
        mTvSummary=(TextView)findViewById(R.id.tv_book_intro_content);
        mTvContent=(TextView)findViewById(R.id.tv_book_mulu_content);
    }

    public void getRequestData(String isbn){
        RequestParams params=new RequestParams();
        BaseAsyncHttp.getReq("/v2/book/isbn/"+isbn,params,new HttpResponseHandler() {
            @Override
            public void jsonSuccess(JSONObject resp) {
                mBook=new Book();
                mBook.setId(resp.optString("id"));
                mBook.setRate(resp.optJSONObject("rating").optDouble("average"));
                String authors="";
                for (int j=0;j<resp.optJSONArray("author").length();j++){
                    authors=authors+" "+resp.optJSONArray("author").optString(j);
                }
                mBook.setAuthor(authors);
                String tags="";
                for (int j=0;j<resp.optJSONArray("tags").length();j++){
                    tags=tags+" "+resp.optJSONArray("tags").optJSONObject(j).optString("name");
                }
                mBook.setTag(tags);
                mBook.setAuthorInfo(resp.optString("author_intro"));
                mBook.setBitmap(resp.optString("image"));
                mBook.setId(resp.optString("id"));
                mBook.setTitle(resp.optString("title"));
                mBook.setPublisher(resp.optString("publisher"));
                mBook.setPublishDate(resp.optString("pubdate"));
                mBook.setISBN(resp.optString("isbn13"));
                mBook.setSummary(resp.optString("summary"));
                mBook.setPage(resp.optString("pages"));
                mBook.setPrice(resp.optString("price"));
                mBook.setContent(resp.optString("catalog"));
                mBook.setUrl(resp.optString("ebook_url"));
                updateToView();
            }
        });
    }

    private void updateToView(){
        mTvAuthor.setText(mBook.getAuthor());
        mTvPublisher.setText(mBook.getPublisher());
        mTvDate.setText(mBook.getPublishDate());
        mTvIsbn.setText(mBook.getISBN());
        mTvRate.setText(mBook.getRate()+"分");
        mTvPrice.setText(mBook.getPrice());
        mTvPage.setText(mBook.getPage());
        mTvSummary.setText(mBook.getSummary());
        mTvContent.setText(mBook.getContent());
        mTvtags.setText(mBook.getTag());
        ImageLoader.getInstance().displayImage(mBook.getBitmap(),mIvIcon);
    }

    private void initBtn(){
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
        PromotedActionsLibrary promotedActionsLibrary = new PromotedActionsLibrary();
        promotedActionsLibrary.setup(getApplicationContext(), frameLayout);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        promotedActionsLibrary.addItem(getResources().getDrawable(R.drawable.timeline),new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookViewActivity.this,Share2Weixin.class);
                intent.putExtra("url",mBook.getUrl());
                intent.putExtra("score",mBook.getRate()+"");
                intent.putExtra("picurl",mBook.getBitmap());
                intent.putExtra("name",mBook.getTitle());
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
        promotedActionsLibrary.addItem(getResources().getDrawable(R.drawable.weixin), new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookViewActivity.this,Share2Weibo.class);
                intent.putExtra("url",mBook.getUrl());
                intent.putExtra("score",mBook.getRate()+"");
                intent.putExtra("picurl",mBook.getBitmap());
                intent.putExtra("name",mBook.getTitle());
                startActivity(intent);
            }
        });
        promotedActionsLibrary.addItem(getResources().getDrawable(R.drawable.weibo), new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookViewActivity.this,Share2Weixin.class);
                intent.putExtra("url",mBook.getUrl());
                intent.putExtra("score",mBook.getRate()+"");
                intent.putExtra("picurl",mBook.getBitmap());
                intent.putExtra("name",mBook.getTitle());
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });
        promotedActionsLibrary.addMainItem(getResources().getDrawable(android.R.drawable.ic_input_add));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setHomeButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
